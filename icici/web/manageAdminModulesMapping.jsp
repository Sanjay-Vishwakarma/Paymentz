<%@ page import="com.directi.pg.Database"%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 3/9/15
  Time: 9:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title> Admin Module Management</title>
    <link rel="stylesheet" type="text/css" href="/merchant/stylepoptuk/poptuk.css">
    <script type='text/javascript' src='/merchant/stylepoptuk/poptuk.js'></script>
    <script src="/icici/css/am_multipleselection/bootstrap-multiselect.js"></script>
    <link href="/icici/css/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet"/>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script>
        function lancer(terminal)
        {
            poptuk(document.getElementById('poptuk' + terminal).style.display = "", terminal);
        }
    </script>
    <style type="text/css">
        #checkboxes {
            display: none;
            border: 1px #dadada solid;
            position: absolute;
            width: 100%;
            background-color: #ffffff;
            z-index: 1;
            height: 130px;
            overflow-x: auto;
        }

        #checkboxes label {
            display: block;
        }

        #checkboxes label:hover {
            background-color: #1e90ff;
        }

        input[type="checkbox"] {
            width: 18px; /*Desired width*/
            height: 18px; /*Desired height*/
        }

        .icheckbox_square-aero {
            margin: 3px 5px;
        }

        /********************************************************************************/

        .multiselect-container > li {
            padding: 0;
            margin-left: 31px;
        }

        .open > #multiselect-id.dropdown-menu {
            display: block;
        }

        .multiselect-container > li > a > label {
            margin: 0;
            height: 24px;
            padding-left: 1px;
        !important;
            text-align: left;
        }

        span.multiselect-native-select {
            position: relative;
        }

        @supports (-ms-ime-align:auto) {
            span.multiselect-native-select {
                position: static !important;
            }
        }

        select[multiple], select[size] {
            height: auto;
            border-color: rgb(169, 169, 169);
        }

        .widget .btn-group {
            z-index: 1;
        }

        .btn-group, .btn-group-vertical {
            position: relative;
            vertical-align: middle;
            border-radius: 4px;
        }

        #mainbtn-id.btn-default {
            color: #333;
            background-color: #fff;
            border-color: #333;
            padding: 6px;
        }

        .btn-group > .btn:first-child {
            margin-left: 0;
        }

        .btn-group > .btn:first-child {
            margin-left: 0;
        }

        .btn-group > .btn, .btn-group-vertical > .btn {
            position: relative;
            float: left;
        }

        .multiselect-container {
            position: absolute;
            list-style-type: none;
            margin: 0;
            padding: 0;
            height: 225px;
            overflow-y: scroll;
        }

        #multiselect-id.dropdown-menu {
            position: absolute;
            top: 100%;
            left: 0;
            z-index: 500;
            display: none;
            float: left;
            min-width: 160px;
            font-size: 14px;
            font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
            list-style: none;
            background-color: #fff;
            border: 1px solid #ccc;
            border: 1px solid rgba(0, 0, 0, 0.15);
            border-radius: 4px;
            -webkit-box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
            background-clip: padding-box;
        }

        #mainbtn-id .multiselect-selected-text {
            font-size: 12px;
            font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
        }

        #mainbtn-id.btn-default, #mainbtn-id.btn-default:focus, #mainbtn-id.btn-default.focus, #mainbtn-id.btn-default:active, #mainbtn-id.btn-default.active, .open > .dropdown-toggle#mainbtn-id.btn-default {
            color: #333;
            /*color: #fff;*/
            background-color: white !important;
            border-color: #ddd !important;
            text-align: left;
            width: 187px;
        !important;
        }

        .multiselect-container .active > a > label {
            color: #fff;
        !important;
        }

        .btn .caret {
            position: absolute;
            display: inline-block;
            width: 0px;
            height: 1px;
            margin-left: 2px;
            vertical-align: middle;
            border-top: 7px solid;
            border-right: 4px solid transparent;
            border-left: 4px solid transparent;
            float: right;
            margin-top: 5px;
            box-sizing: inherit;
            top: 13px;
            right: 5px;
            margin-top: -2px;
        }

        .fa-chevron-down {
            position: absolute;
            right: 0px;
            top: 0px;
            margin-top: -2px;
            vertical-align: middle;
            float: right;
            font-size: 9px;
        }

        #mainbtn-id {
            overflow: hidden;
            display: block;
        }

        .cardLimitCheck, .cardAmountLimitCheck, .amountLimitCheck {
            color: #001962;
            text-valign: center;
            font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
            font-size: 12px;
            FONT-WEIGHT: normal;
            height: 25px;
            -webkit-border-radius: 4px;
            -moz-border-radius: 4px;
            border-radius: 4px;
        }

        /********************************************************************************/
    </style>
</head>
<body>

<script type="text/javascript">
    $(function ()
    {
        $(document).ready(function ()
        {
            $(".caret").addClass('icon2');
            $('.multiselect-selected-text').addClass("filter-option pull-left");
            firefox = navigator.userAgent.search("Firefox");
            if (firefox > -1)
            {
                $('.icon2').removeClass("caret");
                $('.icon2').addClass("fa fa-chevron-down");
                $('.icon2').css({
                    "height": "30px",
                    "width": "17px",
                    "text-align": "center",
                    "background-color": "#E6E2E2",
                    "padding-top": "6px",
                    "margin-top": "0px",
                    "border": "1px solid #C7BFBF"
                });
                $('.dropdown-toggle').css({"padding": "0px", "vertical-align": "middle", "height": "25px"});
                $('.tr0 .dropdown-toggle .icon2').css({"background-color": "white", "border": "0px"});
                $('.tr1 .dropdown-toggle .icon2').css({"background-color": "white", "border": "0px"});
                $('.multiselect-selected-text').css({
                    "padding-top": "4px",
                    "padding-bottom": "10px",
                    "padding-left": "10px",
                    "vertical-align": "middle"
                });
            }
        });
        $('#adminlist').multiselect({
            buttonText: function (options, select)
            {
                var labels = [];
                if (options.length === 0)
                {
                    labels.pop();
                    document.getElementById('adminid').value = labels;
                    return 'Select Admin User';
                }
                else
                {
                    options.each(function ()
                    {
                        labels.push($(this).val());
                    });
                    document.getElementById('adminid').value = labels;
                    // return labels.join(', ') + '';
                    return 'Select Admin User';
                }
            }
        });

        $('#adminmodulelist').multiselect({
            buttonText: function (options, select)
            {
                var labels = [];
                if (options.length === 0)
                {
                    labels.pop();
                    document.getElementById('moduleid').value = labels;
                    return 'Select Admin Module';
                }
                else
                {
                    options.each(function ()
                    {
                        labels.push($(this).val());
                    });
                    document.getElementById('moduleid').value = labels;
                    //return labels.join(', ') + '';
                    return 'Select Admin Module';
                }
            },
            includeSelectAllOption: true
        });
    });
</script>
<%!
    Logger logger=new Logger("manageAdminModulesMapping");
%>
<% Logger logger = new Logger("manageAdminModulesMapping.jsp");
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String adminid="";
        String moduleid="";
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Manage Admin Module Mapping
                <div style="float: right;">
                    <form action="/icici/adminModuleMappingList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Module Mapping List" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Module Mapping List
                        </button>
                    </form>
                </div>
            </div>
            <br>
            <form action="/icici/servlet/ManageAdminModuleMapping?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table align="center" width="65%" cellpadding="2" cellspacing="2">

                    <tbody>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                <tr><td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Admin User:</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select id="adminlist" size="1" class="multiselect txtboxsmall" multiple="multiple" style="width:186px">
                                            <option value="" default>Select Admin User</option>
                                            <%
                                                Connection conn = null;
                                                PreparedStatement pstmt = null;
                                                ResultSet rs = null;
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String  query = "select * from admin";
                                                    pstmt = conn.prepareStatement( query );
                                                    rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\""+rs.getInt("adminid")+"\">"+rs.getInt("adminid")+" - "+rs.getString("login")+"</option>");
                                                    }
                                                }
                                                catch (Exception e)
                                                {
                                                    logger.error("Exception"+e);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>
                                        <input type="hidden" id="adminid" name="adminid" value="">

                                    </td>
                                <tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Admin Module:</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select id="adminmodulelist" size="1" class="multiselect txtboxsmall" multiple="multiple" style="width:186px">
                                           <%-- <option value="" default>Select Admin Module</option>--%>
                                            <%
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String query = "select * from admin_modules_master";
                                                    pstmt = conn.prepareStatement(query);
                                                    rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\"" + rs.getInt("moduleid") +  "\">" + rs.getInt("moduleid")+ "-"+rs.getString("modulename") + "</option>");
                                                    }
                                                }
                                                catch (Exception e)
                                                {
                                                    logger.error("Exception"+e);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>
                                        <input type="hidden" id="moduleid" name="moduleid" value="">
                                        </select>

                                    </td>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb">
                                        <button type="submit" class="buttonform" value="Save">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;Save
                                        </button>
                                    </td>
                                </tr>

                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>

                                </tbody>
                            </table>

                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>
    </div>
</div>
<%
    String message=(String)request.getAttribute("message");
    Functions functions=new Functions();
    if(functions.isValueNull(message))
    {
%>
<div class="reporttable">
    <%
        out.println(Functions.NewShowConfirmation("Result",message));
    %>
</div>
<%
        }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>