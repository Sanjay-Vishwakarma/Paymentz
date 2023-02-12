<%@ include file="top.jsp" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.RuleMasterVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.FraudRuleManager" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    session.setAttribute("submit", "Internal Fraud Configuration");
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);

    String internalFraudConfiguration_Default_Internal_Rule_Configuration = rb1.getString("internalFraudConfiguration_Default_Internal_Rule_Configuration");
    String internalFraudConfiguration_RuleID = rb1.getString("internalFraudConfiguration_RuleID");
    String internalFraudConfiguration_RuleName = rb1.getString("internalFraudConfiguration_RuleName");
    String internalFraudConfiguration_Rule_Description = rb1.getString("internalFraudConfiguration_Rule_Description");
    String internalFraudConfiguration_Score = rb1.getString("internalFraudConfiguration_Score");
    String internalFraudConfiguration_Limit = rb1.getString("internalFraudConfiguration_Limit");
    String internalFraudConfiguration_Status = rb1.getString("internalFraudConfiguration_Status");
    String internalFraudConfiguration_Update_Selected = rb1.getString("internalFraudConfiguration_Update_Selected");
%>
<%!
    private static Logger log = new Logger("internalFraudConfiguration.jsp");
%>
<html>
<head>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script language="javascript">
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("ruleid");
            var total_boxes = checkboxes.length;
            for (i = 0; i < total_boxes; i++)
            {
                checkboxes[i].checked = flag;
            }
        }
        function doChanges(data, ruleid)
        {
            if (data.checked)
            {
                document.getElementById('cbk_' + ruleid).value = "Enable";
            }
            else
            {
                document.getElementById('cbk_' + ruleid).value = "Disable";
            }
            document.getElementById('status_' + ruleid).value = document.getElementById('cbk_' + ruleid).value;
        }
        function DoUpdate()
        {
            var checkboxes = document.getElementsByName("ruleid");
            var total_boxes = checkboxes.length;
            flag = false;
            for (i = 0; i < total_boxes; i++)
            {
                if (checkboxes[i].checked)
                {
                    flag = true;
                    break;
                }
            }
            if (!flag)
            {
                alert("select at least one rule");
                return false;
            }
            if (confirm("Do you really want to update all selected rule."))
            {
                document.update.submit();
            }
        }
    </script>
    <style type="text/css">
        input[type=radio], input[type=checkbox] {
            transform: scale(2);
            -ms-transform: scale(2);
            -webkit-transform: scale(2);
            padding: 10px;
        }

        table#paginateid td {
            vertical-align: initial !important;
        }

        @media (max-width: 640px) {
            table thead {
                display: table-row-group;
            }

            table thead tr th:first-child {
                display: block !important;
            }

            table thead tr th:nth-of-type(2) {
                display: none;
            }

            table thead tr th:nth-of-type(3) {
                display: none;
            }

            table thead tr th:nth-of-type(4) {
                display: none;
            }

            table thead tr th:nth-of-type(5) {
                display: none;
            }
        }
    </style>
    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <script type="">
        $(document).ready(function ()
        {
            //alert("hi checkbox");
            $('div.icheckbox_square-aero').remove();
            $('ins.iCheck-helper').remove();
        });
    </script>
</head>
<title><%=company%> Fraud Management> Default Internal Rules</title>

<body>

<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        String updateMsg = (String) request.getAttribute("updateMsg");
        String statusMsg = String.valueOf(request.getAttribute("statusMsg"));
        String errormsg = String.valueOf(request.getAttribute("errormsg"));

        Functions functions = new Functions();

        List<RuleMasterVO> internalLevelRuleMapping = (List<RuleMasterVO>) request.getAttribute("internalLevelRuleMapping");


        if (internalLevelRuleMapping.size() > 0)
        {
%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i
                                    class="fa fa-th-large"></i>&nbsp;&nbsp;<%=internalFraudConfiguration_Default_Internal_Rule_Configuration%>
                            </strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">

                            <%
                                if (functions.isValueNull(errormsg))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg.toString() + "</h5>");
                                }
                            %>

                            <form name="update"
                                  action="/partner/net/InternalFraudConfiguration?action=update&ctoken=<%=ctoken%>"
                                  method="post" accept-charset="UTF-8">

                                <table id="myTable" class="display table table-striped table-bordered" width="100%"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                                    <script type="">
                                        $(document).ready(function ()
                                        {
                                            $(".thcheckbox").append("<input type=\"checkbox\" onClick=\"ToggleAll(this);\" name=\"alltrans\">")
                                        });
                                    </script>

                                    <thead>
                                    <tr style="background-color: #7eccad !important;color: white;">
                                        <th style="text-align: center;" class="thcheckbox"></th>
                                        <th style="text-align: center;"><%=internalFraudConfiguration_RuleID%>
                                        </th>
                                        <th style="text-align: center;"><%=internalFraudConfiguration_RuleName%>
                                        </th>
                                        <th style="text-align: center;"><%=internalFraudConfiguration_Rule_Description%>
                                        </th>
                                        <th style="text-align: center;"><%=internalFraudConfiguration_Score%>
                                        </th>
                                        <th style="text-align: center;"><%=internalFraudConfiguration_Limit%>
                                        </th>
                                        <th style="text-align: center;"><%=internalFraudConfiguration_Status%>
                                        </th>
                                    </tr>
                                    </thead>

                                    <tbody class="checkclassfirst1">
                                    <%
                                        for (RuleMasterVO ruleMasterVO : internalLevelRuleMapping)
                                        {
                                            String ext = "";
                                            if ("Enable".equals(ruleMasterVO.getDefaultStatus()))
                                            {
                                                ext = "checked";
                                            }
                                    %>
                                    <script type="">
                                        $(function ()
                                                {
                                                    $(".checkclassfirst1").append("<tr>" +
                                                            "<td style=\"text-align: center; vertical-align: middle;\" > <input type=\"checkbox\" class=\"CheckBoxClass1\" name=\"ruleid\" value=\"<%=ruleMasterVO.getRuleId()%>\"></td>" +
                                                            "<td  data-label=\"Rule ID\" style=\"text-align: center; vertical-align: middle;\" ><%=ruleMasterVO.getRuleId()%></td>" +
                                                            "<td  data-label=\"Rule Name\" style=\"text-align: center; vertical-align: middle;\" ><%=ruleMasterVO.getRuleName()%></td>" +
                                                            "<td  data-label=\"Rule Name\" style=\"text-align: center; vertical-align: middle;\" ><%=ruleMasterVO.getRuleDescription()%></td>" +
                                                            "<td data-label=\"Score\" style=\"text-align: center;\" ><input type=\"text\"  style=\"text-align: center\" maxlength=\"2\" class=\"form-control\" style=\"width:75px\" valign=\"middle\" name=\"score_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getDefaultScore()%>\" class=\"txtbox\"></td>" +

                                                            <%
                                                            if(ruleMasterVO.getRuleName().equalsIgnoreCase("Block_Card_By_Usage"))
                                                            {
                                                            %>
                                                            "<td data-label=\"Limit\" style=\"text-align: center;\" ><select  style=\"font-size: 16px;height: 37px;width: 100%;\" class=\"txtboxtabel\" name=\"value_<%=ruleMasterVO.getRuleId()%>\">" +
                                                            <%
                                                           if("Personal".equals(ruleMasterVO.getDefaultValue()))
                                                           {
                                                           %>
                                                            "<option value=\"Personal\"  selected>Personal</option>" +
                                                            "<option value=\"Commercial\">Commercial</option>" +
                                                            <%
                                                             }
                                                            else if("Commercial".equals(ruleMasterVO.getDefaultValue()))
                                                            {
                                                            %>
                                                            "<option value=\"Personal\">Personal</option>" +
                                                            "<option value=\"Commercial\" selected>Commercial</option>" +
                                                            <%
                                                              }
                                                              else
                                                              {
                                                            %>
                                                            "<option value=\"Personal\">Personal</option>" +
                                                            "<option value=\"Commercial\">Commercial</option>" +
                                                            <%
                                                              }
                                                              %>
                                                    "</select>"+
                                                            <%
                                                              }
                                                                    else if(ruleMasterVO.getRuleName().equalsIgnoreCase("Block_Card_By_Type"))
                                                    {
                                                    %>
                                                            "<td data-label=\"Limit\" style=\"text-align: center;\" ><select  style=\"font-size: 16px;height: 37px;width: 100%;\" class=\"txtboxtabel\" name=\"value_<%=ruleMasterVO.getRuleId()%>\">" +
                                                            <%

                                                                    Set cardSet = new TreeSet();
                                                                    cardSet =(Set)request.getAttribute("cardSet");
                                       String isSelected = "";
                                       for(Object cardType : cardSet)
                                       {
                                         if(cardType.toString().equalsIgnoreCase(ruleMasterVO.getDefaultValue())){
                                           isSelected = "selected";
                                           }
                                         else
                                           isSelected = "";
                                         String card=cardType.toString().replace(" ","");
                                    %>
                                                            "<option value=<%=card%> <%=isSelected%>><%=card%></option>" +
                                                            <%
                                                                                                    }

                                                                                              }
                                                                                              else
                                                                                              {
                                                                                                  %>
                                                            "<td data-label=\"Limit\" style=\"text-align: center;\" ><input type=\"text\"  style=\"text-align: center\" maxlength=\"30\" class=\"form-control\" style=\"width:75px\" valign=\"middle\" name=\"value_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getDefaultValue()==null?"":ruleMasterVO.getDefaultValue()%>\" class=\"txtbox\"></td>" +
                                                            <%
                                                            }
                                                            %>
                                                            "<td data-label=\"Status\" style=\"text-align: center;\"><input type=\"hidden\" name=\"name_<%=ruleMasterVO.getRuleId()%>\" id=\"name_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getRuleName()%>\"><input type=\"hidden\" name=\"status_<%=ruleMasterVO.getRuleId()%>\" id=\"status_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getDefaultStatus()%>\"><input type=\"checkbox\" id=\"cbk_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getDefaultStatus()%>\" valign=\"middle\" name=\"status\" <%=ext%> onclick=\"doChanges(this,<%=ruleMasterVO.getRuleId()%>)\"> </td>" +
                                                            "</tr>"
                                                    )
                                                    ;
                                                }
                                        )
                                        ;
                                    </script>
                                    <%
                                        }
                                    %>
                                    </tbody>
                                </table>

                                <div align="center">
                                    <button type="button" value="Update" class="btn btn-default"
                                            onClick="return DoUpdate();">
                                        <%=internalFraudConfiguration_Update_Selected%>
                                    </button>
                                </div>

                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <br>
                <%
                }
                else
                {

                    out.println("<div class=\"content-page\">");
                    out.println("<div class=\"content\">");
                    out.println("<div class=\"page-heading\">");
                    out.println("<div class=\"row\">");
                    out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
                    out.println("<div class=\"widget\">");
                    out.println("<div class=\"widget-header transparent\">\n" +
                            "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;Fraud Rule Status</strong></h2>\n" +
                            "                                <div class=\"additional-btn\">\n" +
                            "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                            "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                            "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                            "                                </div>\n" +
                            "                            </div>");
                    out.println("<div class=\"widget-content padding\">");
                    out.println(Functions.NewShowConfirmation1("Result", "No data found"));
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");

                }

    }
    else
    {
        response.sendRedirect("/partner/logout.jsp");
        return;
    }

%>
            <script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
            <script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
            <link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
            <link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
<%!
    public static String nullToStr(String str)
    {
        if (str == null)
            return "";
        return str;
    }
%>