<%--
  Created by IntelliJ IDEA.
  User: mukesh.a
  Date: 1/23/14
  Time: 12:52 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.*" %>
<%@ page import="com.logicboxes.util.LoadProperties" %>
<%@ page import="org.codehaus.jettison.json.JSONObject" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%!
    Logger logger = new Logger("addNewGatewayAccount.jsp");
    Functions functions = new Functions();
%>
<html>
<head><title>Add New Gateway Account</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <link rel="stylesheet" type="text/css" href="/merchant/stylepoptuk/poptuk.css">
    <script type='text/javascript' src='/merchant/stylepoptuk/poptuk.js'></script>
    <script src="/icici/css/am_multipleselection/bootstrap-multiselect.js"></script>
    <link href="/icici/css/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet"/>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script type="text/javascript">
        var lablevalues = new Array();
        function ChangeFunction(Value , lable){
            var finalvalue=lable+"="+Value;
            lablevalues.push(finalvalue);
            document.getElementById("onchangedvalue").value = lablevalues;
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
            font-size: 16px;
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
        .chkbox{
            border: 2px solid #bcc8c6;
            font-weight:bold;
            margin-right: -15px;
            margin-left: -15px;
            height: 34px;
        }

    </style>
    <script type="text/javascript">

        function gettabledata()
        {

            var select = document.getElementById("pgtypeid");
            console.log(select);
            var answer = select.options[select.selectedIndex].value;
            var bankid = document.getElementById("bankid").value;
            var temp = new Array();
            if(bankid != "")
            {
                temp = bankid.split(",");
            }
            temp.push(answer);
            console.log(temp.length);
            if( temp.length <= 1 || answer == "")
            {
                document.addtype.action = "/icici/addNewGatewayAccount.jsp?pgtypeid=" + answer;
                document.addtype.submit();
            }
        }

        function check()
        {
            var msg = "";
            var flag = false;
            if (document.getElementById("pgtypeid").value.length == 0)
            {
                msg = msg + "\nPlease Select Gateway Type.";
                flag = true;
                //document.getElementById("pgtypeid").focus();
            }

            if (document.getElementById("merchantid").value.length == 0)
            {
                msg = msg + "\nPlease Enter MerchantId.";
                flag = true;
                //document.getElementById("merchantid").focus();
            }

            if (document.getElementById("aliasname").value.length == 0)
            {
                msg = msg + "\nPlease Enter AliasName.";
                flag = true;
                //document.getElementById("aliasname").focus();
            }

            if (document.getElementById("displayname").value.length == 0)
            {
                msg = msg + "\n Please Enter DisplayName.";
                flag = true;
                //document.getElementById("displayname").focus();
            }
            if (flag == true)
            {
                alert(msg);
                return false;
            }
            else
            {
                document.addtype.submit();
                return true;
            }
        }

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

        });

    </script>
    <style>
        .txtbox {
            width: 300px;
        }
    </style>
</head>

<body>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Add GateWay Account
            </div>
                <%

        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (!com.directi.pg.Admin.isLoggedIn(session))
            {
                response.sendRedirect("/icici/logout.jsp");
                return;
            }
             String flag="false";
        String bankid = request.getParameter("bankid");
        ResourceBundle gatewayLabelRB=LoadProperties.getProperty("com.directi.pg.GatewayLabel");
        String messaqe = "";
        messaqe = request.getParameter("message");

        if(messaqe != null){
             out.println("<center><font class=\"textb\">"+ messaqe + "</font></center>");
             flag="true";
             }

        String gateway_table_name = "";
        String gateway = "";
        String tableColumn = "";
        StringBuilder dataStringBuilder = new StringBuilder();
        Connection conn = null;
        String pgtypeid ="";
        String partnerid ="";
        String agentid ="";
        String role="Admin";
			String username=(String)session.getAttribute("username");
			String actionExecutorId=(String)session.getAttribute("merchantid");
			String actionExecutorName=role+"-"+username;
			String daily_amount_range= "500.00-1000.00";

        JSONObject gatewayLabelJSON=new JSONObject();
        LinkedHashMap<Integer, String> gatewayTypes =null;

       try
       {
           if(request.getParameter("partnerid") != null && request.getParameter("partnerid").length() > 0)
            partnerid = request.getParameter("partnerid");

            if(request.getParameter("agentid") != null && request.getParameter("agentid").length() > 0)
            agentid = request.getParameter("agentid");

           if(request.getParameter("pgtypeid") != null && request.getParameter("pgtypeid").length() > 0){
              pgtypeid = request.getParameter("pgtypeid");
              gatewayTypes = GatewayTypeService.loadGatewayTypesByid(pgtypeid);
              }
              else{
                  gatewayTypes = GatewayTypeService.loadGatewayTypesAll();
              }

            if(pgtypeid.length() > 0)
            {
                conn = Database.getConnection();
                String tableNameQuery = "select gateway_table_name,gateway from gateway_type where pgtypeid = ?";
                logger.debug("tableNameQuery::"+tableNameQuery);
                PreparedStatement preparedStatement = conn.prepareStatement(tableNameQuery);
                preparedStatement.setInt(1,Integer.parseInt(pgtypeid));
                ResultSet resultset = preparedStatement.executeQuery();

                while(resultset.next())
                {
                    gateway_table_name = resultset.getString("gateway_table_name");
                    gateway = resultset.getString("gateway");
                }
                try{
                 String json="";
                if(gatewayLabelRB.containsKey(gateway))
                  json=gatewayLabelRB.getString(gateway);
                if(functions.isValueNull(json) && json.contains("{"))
                    gatewayLabelJSON=new JSONObject(json);
                }catch (Exception e){
                logger.error("Property not found-->",e);
                }
                if(functions.isValueNull(gateway_table_name) && gateway_table_name.length() > 0)
                {
                    String tableDataQuery = "select * from " + gateway_table_name + " where 1=1 limit 1";
                    logger.debug(" tableDataQuery::"+tableDataQuery);
                    PreparedStatement preparedStatementData = conn.prepareStatement(tableDataQuery);
                    ResultSet tableMetaDataRs = preparedStatementData.executeQuery();
                    Integer count = tableMetaDataRs.getMetaData().getColumnCount();

                    while(tableMetaDataRs.next())
                    {
                        for(int i=1;i<=count;i++)
                        {
                            String columnName = tableMetaDataRs.getMetaData().getColumnName(i);
                            String columnNameLabel = tableMetaDataRs.getMetaData().getColumnName(i);
                            int columnNameSize = tableMetaDataRs.getMetaData().getColumnDisplaySize(i);
                            //below line has added to make inital later as capital later.
                            columnNameLabel=columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1);
                            String isOptional="true";
                            if(tableMetaDataRs.getMetaData().isNullable(i)==0)
                            {
                              columnNameLabel=columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1)+"*";
                              isOptional="false";
                            }
                            String columnType = tableMetaDataRs.getMetaData().getColumnTypeName(i);

                           if(!columnName.equalsIgnoreCase("id") && !columnName.equalsIgnoreCase("accountid"))
                            {
                                dataStringBuilder.append("<tr>");
                                dataStringBuilder.append("<td style=\"padding: 3px\" class=\"textb\" width=\"2%\" >&nbsp;</td>");
                                dataStringBuilder.append("<td width='43%'><span class='textb'>" + columnNameLabel + "</span></td>");
                                dataStringBuilder.append("<td style=\"padding: 3px\" width=\"5%\" class=\"textb\">:</td>");
                                dataStringBuilder.append("<td width='50%' class='textb'><input type=\"hidden\" name=\""+columnName+"_isOptional\" value=\""+isOptional+"\"><input type=\"hidden\" name=\""+columnName+"_size\" value=\""+columnNameSize+"\"><input class='textb' size='20' type='text' name='" + columnName + "' value=''> ( " + columnType + " - "+columnNameSize+") </td>");
                                dataStringBuilder.append("</tr>");
                            }
                                tableColumn += columnName + ",";
                        }
                    }
                    if(tableColumn.length() > 0)
                        tableColumn = tableColumn.substring(0,tableColumn.length()-1);
                }
            }
            else
            {  logger.error("pgynot selectd"); }
      }
      catch(Exception e)
      {
          logger.error("Sql Exception in addNewGatewayAccount:",e);
      }
    finally
       {
           Database.closeConnection(conn);
       }

     /*if(messaqe != null && messaqe.length() > 0)*/
%>
            <form name="addtype" action="/icici/servlet/addGatewayAccounts?ctoken=<%=ctoken%>" method="post"
                  name="form1" onsubmit="return check()">
                <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <input type="hidden" value="true" name="isSubmitted">
                <input type="hidden" name="gatewaytablename" value="<%=gateway_table_name%>">
                <input type="hidden" name="columnnames" value="<%=tableColumn%>">
                <table align="center" width="65%" cellpadding="2" cellspacing="2">
                    <tbody>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Pgtype Id *</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select id="pgtypeid" name="pgtypeid" class="multiselect txtboxsmall"
                                                multiple="multiple" size="1" onchange ='gettabledata()'>
                                            <option value="" default>Display all Banks</option>
                                            <%
                                                for (Integer id : gatewayTypes.keySet())
                                                {
                                                    String st = "";
                                                    String name = gatewayTypes.get(id);
                                                    if (name != null)
                                                    {
                                                      /*  if(pgtypeid.equalsIgnoreCase(gatewayType.getPgTypeId())){*/%>
                                            <%--<option value="<%=gatewayType.getPgTypeId()%>" selected><%=gatewayType.getName()%></option>--%>
                                            <%--<%}else%>--%>
                                            <option value="<%=id%>"><%=gatewayTypes.get(id)%>
                                            </option>
                                            <%
                                                    }
                                                }
                                            %>
                                            <%--<%=sb.toString()%>--%>
                                        </select>
                                        <input type="hidden" id="bankid" name="bankid" value="<%=pgtypeid%>">
                                        <script type="application/javascript">
                                            <%
                                            if(flag.equals("true")){
                                                if(functions.isValueNull(bankid)){%>
                                            var bankid = "<%=bankid%>";
                                            var temp = new Array();
                                            if(bankid != "")
                                            {
                                                temp = bankid.split(",");
                                            }
                                            for(var i=0; i <= temp.length ; i++)
                                            {
                                                console.log(temp[i]);
                                                $("#pgtypeid option[value='"+temp[i]+"']").prop('selected', true);
                                            }
                                            <%
                                              }
                                                  }else{
                                                          if(functions.isValueNull(pgtypeid)){%>
                                            $("#pgtypeid option[value='<%=pgtypeid%>']").prop('selected', true);
                                            <%
                                            }
                                            }
                                            %>
                                            $('#pgtypeid').multiselect({
                                                buttonText: function (options, select)
                                                {
                                                    var labels = [];
                                                    if (options.length === 0)
                                                    {
                                                        labels.pop();
                                                        document.getElementById('bankid').value = labels;
                                                        return 'Select PgType ID';
                                                    }
                                                    else
                                                    {
                                                        options.each(function ()
                                                        {
                                                            if($(this).val() != "")
                                                            {
                                                                labels.push($(this).val());
                                                            }
                                                        });
                                                        document.getElementById('bankid').value = labels;
                                                        //return labels.join(', ') + '';
                                                        return 'Select PgType ID';
                                                    }
                                                }
                                            });
                                        </script>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb">
                                        <%
                                            if (gatewayLabelJSON.has("merchantId"))
                                            {
                                                out.println(gatewayLabelJSON.getString("merchantId") + "*");
                                        %>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        Merchant Id *
                                        <%}%>
                                    </td>
                                    <td style="padding: 3px" width="5%" class="textb">:</td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <input class="txtbox" type="Text" maxlength="50" name="merchantid" size="50"
                                               id="merchantid"
                                               value="<%= request.getParameter("merchantid") != null?request.getParameter("merchantid"):"" %>">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Alias Name*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" maxlength="50"
                                                                    name="aliasname" size="50" id="aliasname"
                                                                    value="<%= request.getParameter("aliasname") != null?request.getParameter("aliasname"):"" %>">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Display Name*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" maxlength="50"
                                                                    maxlength=50 name="displayname" size="50"
                                                                    id="displayname"
                                                                    value="<%= request.getParameter("displayname") != null?request.getParameter("displayname"):"" %>">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Is MasterCard Supported</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><select name="ismastercardsupported"
                                                                     id="ismastercardsupported">
                                        <%
                                            String isMasterCard = request.getParameter("ismastercardsupported");
                                            if ("1".equalsIgnoreCase(isMasterCard))
                                            {
                                        %>
                                        <option value='1' selected>Y</option>
                                        <option value='0'>N</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value='1'>Y</option>
                                        <option value='0' selected>N</option>
                                        <% } %>

                                    </select></td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">
                                        <%
                                            if (gatewayLabelJSON.has("shortName"))
                                            {
                                                out.println(gatewayLabelJSON.getString("shortName"));
                                        %>
                                        <%
                                        }
                                        else
                                        {
                                        %>Short Name<%}%></td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" maxlength="50"
                                                                    name="shortname" id="shortname" size="50"
                                                                    value="<%= request.getParameter("shortname") != null?request.getParameter("shortname"):"" %>">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">
                                        <%
                                            if (gatewayLabelJSON.has("site"))
                                            {
                                                out.println(gatewayLabelJSON.getString("site"));
                                        %>
                                        <%
                                        }
                                        else
                                        {
                                        %><%--Site--%> Back Office URL<%}%></td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" maxlength="100" name="site" id="site"
                                                                    size="100"
                                                                    value="<%= request.getParameter("site") != null?request.getParameter("site"):"" %>">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">
                                        <%
                                            if (gatewayLabelJSON.has("path"))
                                            {
                                                out.println(gatewayLabelJSON.getString("path"));
                                        %>
                                        <%
                                        }
                                        else
                                        {
                                        %>Path<%}%></td>
                                    <td style="padding: 3px" class="textb" valign="top">:</td>
                                    <td style="padding: 3px" valign="top">
                                        <input class="txtbox" type="Text" maxlength="100" name="path" id="path"
                                               size="100"
                                               value="<%= request.getParameter("path") != null?request.getParameter("path"):"" %>">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">
                                        <%
                                            if (gatewayLabelJSON.has("username"))
                                            {
                                                out.println(gatewayLabelJSON.getString("username"));
                                        %>
                                        <%
                                        }
                                        else
                                        {
                                        %><%--Username--%> API Name<%}%></td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" maxlength="50"
                                                                    name="username" id="username" size="50"
                                                                    value="<%= request.getParameter("username") != null?request.getParameter("username"):"" %>">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">
                                        <%
                                            if (gatewayLabelJSON.has("password"))
                                            {
                                                out.println(gatewayLabelJSON.getString("password"));
                                        %>
                                        <%
                                        }
                                        else
                                        {
                                        %><%--Password--%>API Key<%}%></td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" maxlength="255"
                                                                    name="passwd" id="passwd" size="25"
                                                                    value="<%= request.getParameter("passwd") != null?request.getParameter("passwd"):"" %>">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">
                                        <%
                                            if (gatewayLabelJSON.has("chargeBackPath"))
                                            {
                                                out.println(gatewayLabelJSON.getString("chargeBackPath") + "*");
                                        %>
                                        <%
                                        }
                                        else
                                        {
                                        %>ChargeBack Path<%}%></td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" maxlength="100"
                                                                    name="chargebackpath" id="chargebackpath" size="100"
                                                                    value="<%= request.getParameter("chargebackpath") != null?request.getParameter("chargebackpath"):"" %>">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">isCvv Required</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">

                                        <select name="iscvvrequired" id="iscvvrequired">
                                            <%
                                                String iscvvrequired = request.getParameter("iscvvrequired");
                                                if ("1".equalsIgnoreCase(iscvvrequired))
                                                {
                                            %>
                                            <option value='1' selected>Y</option>
                                            <option value='0'>N</option>
                                            <%
                                            }
                                            else{
                                            %>
                                            <option value='1'>Y</option>
                                            <option value='0' selected>N</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Card Limit check</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">

                                        <select name="cardLimitCheck" id="cardLimitCheck">
                                            <%
                                                String cardLimitCheck= request.getParameter("cardLimitCheck");
                                                if ("account_Level".equalsIgnoreCase(cardLimitCheck))
                                                {
                                            %>
                                            <option value='N'>N</option>
                                            <option value='account_Level' selected>Account Level</option>
                                            <option value='mid_Level'>MID Level</option>
                                            <%
                                            }else if("mid_Level".equalsIgnoreCase(cardLimitCheck))
                                            {
                                            %>
                                            <option value='N'>N</option>
                                            <option value='account_Level'>Account Level</option>
                                            <option value='mid_Level' selected>MID Level</option>
                                            <%
                                            }else{
                                            %>
                                            <option value='N' selected>N</option>
                                            <option value='account_Level'>Account Level</option>
                                            <option value='mid_Level'>MID Level</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Card Amount Limit check</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">

                                        <select name="cardAmountLimitCheck" id="cardAmountLimitCheck">
                                            <%
                                                String cardAmountLimitCheck= request.getParameter("cardAmountLimitCheck");
                                                if ("account_Level".equalsIgnoreCase(cardAmountLimitCheck))
                                                {
                                            %>
                                            <option value='N'>N</option>
                                            <option value='account_Level' selected>Account Level</option>
                                            <option value='mid_Level'>MID Level</option>
                                            <%
                                            }else if("mid_Level".equalsIgnoreCase(cardAmountLimitCheck))
                                            {
                                            %>
                                            <option value='N'>N</option>
                                            <option value='account_Level'>Account Level</option>
                                            <option value='mid_Level' selected>MID Level</option>
                                            <%
                                            }else{
                                            %>
                                            <option value='N' selected>N</option>
                                            <option value='account_Level'>Account Level</option>
                                            <option value='mid_Level'>MID Level</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Amount Limit check</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">

                                        <select name="amountLimitCheck" id="amountLimitCheck">
                                            <%
                                                String amountLimitCheck= request.getParameter("amountLimitCheck");
                                                if("account_Level".equalsIgnoreCase(amountLimitCheck))
                                                {
                                            %>
                                            <option value='N'>N</option>
                                            <option value='account_Level' selected>Account Level</option>
                                            <option value='mid_Level'>MID Level</option>
                                            <%
                                            }else if("mid_Level".equalsIgnoreCase(amountLimitCheck))
                                            {
                                            %>
                                            <option value='N'>N</option>
                                            <option value='account_Level'>Account Level</option>
                                            <option value='mid_Level' selected>MID Level</option>
                                            <%
                                            }else {
                                            %>
                                            <option value='N' selected>N</option>
                                            <option value='account_Level'>Account Level</option>
                                            <option value='mid_Level'>MID Level</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Daily Amount Limit</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" maxlength="10" name="dailyamountlimit"
                                               id="dailyamountlimit" size="10"
                                               value="<%= request.getParameter("dailyamountlimit") != null?request.getParameter("dailyamountlimit"):"" %>">
                                        <select class ="textb " name='daily_amount_limit_check' >
                                            <%
                                                String daily_amount_limit_check = request.getParameter("daily_amount_limit_check");
                                                if ("Y".equalsIgnoreCase(daily_amount_limit_check))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }else{
                                            %>
                                            <option value="Y">Y</option>
                                            <option value="N" selected>N</option><%}%>
                                        </select>

                                    </td>

                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Daily Amount Range</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="text" maxlength="20" name="daily_amount_range" size="20"
                                               value="<%=daily_amount_range%>">
                                        <select class="textb" name="daily_amount_range_check">
                                            <%
                                                String daily_amount_range_check= request.getParameter("daily_amount_range_check");
                                                if ("Y".equalsIgnoreCase(daily_amount_range_check))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }else {
                                            %>
                                            <option value="Y">Y</option>
                                            <option value="N" selected>N</option>
                                            <%} %>
                                        </select>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Weekly amount Limit</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" maxlength="10" name="weeklyamountlimit"
                                               id="weeklyamountlimit" size="10"
                                               value="<%= request.getParameter("weeklyamountlimit") != null?request.getParameter("weeklyamountlimit"):"" %>">
                                        <select class ="textb " name='weekly_amount_limit_check' >
                                            <%
                                                String weekly_amount_limit_check= request.getParameter("weekly_amount_limit_check");
                                                if ("Y".equalsIgnoreCase(weekly_amount_limit_check))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%} else{%>
                                            <option value="Y">Y</option>
                                            <option value="N" selected>N</option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Montly amount Limit</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" maxlength="10" name="monthlyamountlimit"
                                               id="monthlyamountlimit" size="10"
                                               value="<%= request.getParameter("monthlyamountlimit") != null?request.getParameter("monthlyamountlimit"):"" %>">
                                        <select class ="textb " name='monthly_amount_limit_check' >
                                            <%
                                                String monthly_amount_limit_check= request.getParameter("monthly_amount_limit_check");
                                                if("Y".equalsIgnoreCase(monthly_amount_limit_check))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }else
                                            {
                                            %>
                                            <option value="Y">Y</option>
                                            <option value="N" selected>N</option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Daily Card Limit</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <input class="txtbox" type="Text" maxlength="10" name="dailycardlimit"
                                               id="dailycardlimit" size="10"
                                               value="<%= request.getParameter("dailycardlimit") != null?request.getParameter("dailycardlimit"):"" %>">
                                        <select class ="textb " name='daily_card_limit_check' >
                                            <%
                                                String daily_card_limit_check= request.getParameter("daily_card_limit_check");
                                                if("Y".equalsIgnoreCase(daily_card_limit_check))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }else{
                                            %>
                                            <option value="Y" >Y</option>
                                            <option value="N" selected>N</option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Weekly Card Limit</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <input class="txtbox" type="Text" maxlength="10" name="weeklycardlimit"
                                               id="weeklycardlimit" size="10"
                                               value="<%= request.getParameter("weeklycardlimit") != null?request.getParameter("weeklycardlimit"):"" %>">
                                        <select class ="textb " name='weekly_card_limit_check' >
                                            <%
                                                String weekly_card_limit_check= request.getParameter("weekly_card_limit_check");
                                                if("Y".equalsIgnoreCase(weekly_card_limit_check))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }else{
                                            %>
                                            <option value="Y">Y</option>
                                            <option value="N" selected>N</option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Monthly Card Limit</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <input class="txtbox" type="Text" maxlength="10" name="monthlycardlimit"
                                               id="monthlycardlimit" size="10"
                                               value="<%= request.getParameter("monthlycardlimit") != null?request.getParameter("monthlycardlimit"):"" %>">
                                        <select class ="textb " name='monthly_card_limit_check' >
                                            <%
                                                String monthly_card_limit_check= request.getParameter("monthly_card_limit_check");
                                                if("Y".equalsIgnoreCase(monthly_card_limit_check))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }else{
                                            %>
                                            <option value="Y">Y</option>
                                            <option value="N" selected>N</option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Min Transaction Amount</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" maxlength="10" name="mintransactionamount"
                                               id="mintransactionamount" size="10"
                                               value="<%= request.getParameter("mintransactionamount") != null?request.getParameter("mintransactionamount"):"" %>">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Max Transaction Amount</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" maxlength="10" name="maxtransactionamount"
                                               id="maxtransactionamount" size="10"
                                               value="<%= request.getParameter("maxtransactionamount") != null?request.getParameter("maxtransactionamount"):"" %>">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Daily Card Amount Limit</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" maxlength="10" name="dailycardamountlimit"
                                               id="dailycardamountlimit" size="10"
                                               value="<%= request.getParameter("dailycardamountlimit") != null?request.getParameter("dailycardamountlimit"):"" %>">
                                        <select class ="textb " name='daily_card_amount_limit_check' >
                                            <%
                                                String daily_card_amount_limit_check= request.getParameter("daily_card_amount_limit_check");
                                                if("Y".equalsIgnoreCase(daily_card_amount_limit_check))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }else{
                                            %>
                                            <option value="Y">Y</option>
                                            <option value="N" selected>N</option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Weekly Card Amount Limit</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" maxlength="10" name="weeklycardamountlimit"
                                               id="weeklycardamountlimit" size="10"
                                               value="<%= request.getParameter("weeklycardamountlimit") != null?request.getParameter("weeklycardamountlimit"):"" %>">
                                        <select class ="textb " name='weekly_card_amount_limit_check' >
                                            <%
                                                String weekly_card_amount_limit_check= request.getParameter("weekly_card_amount_limit_check");
                                                if("Y".equalsIgnoreCase(weekly_card_amount_limit_check))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }else{
                                            %>
                                            <option value="Y" >Y</option>
                                            <option value="N" selected>N</option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Month Card Amount Limit</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" maxlength="10" name="monthlycardamountlimit"
                                               id="monthlycardamountlimit" size="10"
                                               value="<%= request.getParameter("monthlycardamountlimit") != null?request.getParameter("monthlycardamountlimit"):"" %>">
                                        <select class ="textb " name='monthly_card_amount_limit_check' >
                                            <%
                                                String monthly_card_amount_limit_check= request.getParameter("monthly_card_amount_limit_check");
                                                if("Y".equalsIgnoreCase(monthly_card_amount_limit_check))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }else{
                                            %>
                                            <option value="Y" >Y</option>
                                            <option value="N" selected>N</option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">isTest Account</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="istest" id="istest">
                                            <%
                                                String istest= request.getParameter("istest");
                                                if("N".equalsIgnoreCase(istest))
                                                {
                                            %>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%
                                            }else{
                                            %>
                                            <option value="N" >N</option>
                                            <option value="Y" selected>Y</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">isActive Account</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="isactive" id="isactive">
                                            <%
                                                String isactive= request.getParameter("isactive");
                                                if ("0".equalsIgnoreCase(isactive))
                                                {
                                            %>
                                            <option value='1'>Y</option>
                                            <option value='0' selected>N</option>
                                            <%
                                            }else {
                                            %>
                                            <option value='1' selected>Y</option>
                                            <option value='0'>N</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Emi Support</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="emiSupport" id="emiSupport">
                                            <%
                                                String emiSupport= request.getParameter("emiSupport");
                                                if ("N".equalsIgnoreCase(emiSupport))
                                                {
                                            %>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%}
                                            else{
                                            %>
                                            <option value="N" >N</option>
                                            <option value="Y" selected>Y</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">is Multiple Refund</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="isMultipleRefund" id="isMultipleRefund">
                                            <%
                                                String isMultipleRefund= request.getParameter("isMultipleRefund");
                                                if ("N".equalsIgnoreCase(isMultipleRefund))
                                                {
                                            %>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%
                                            }else
                                            {
                                            %>
                                            <option value="N" >N</option>
                                            <option value="Y" selected>Y</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb"> Partial Refund</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="PartialRefund" id="PartialRefund">
                                            <%
                                                String PartialRefund= request.getParameter("PartialRefund");
                                                if ("N".equalsIgnoreCase(PartialRefund))
                                                {
                                            %>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%
                                            }else
                                            {
                                            %>
                                            <option value="N" >N</option>
                                            <option value="Y" selected>Y</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb"> Address Validation</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="addressValidation" id="addressValidation">
                                            <%
                                                String addressValidation = request.getParameter("addressValidation");
                                                if ("N".equalsIgnoreCase(addressValidation))
                                                {
                                            %>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N">N</option>
                                            <option value="Y" selected>Y</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Partner*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="partnerid" class="txtbox">
                                            <%
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                                                    PreparedStatement pstmt = conn.prepareStatement(query);
                                                    ResultSet rs = pstmt.executeQuery();
                                                    StringBuffer partnerData = new StringBuffer();
                                                    while (rs.next())
                                                    {
                                                        String selected = "";
                                                        if (rs.getString("partnerId").equals(partnerid))
                                                        {
                                                            selected = "selected";
                                                        }
                                                        partnerData.append("<option value='" + rs.getInt("partnerId") + "' " + selected + ">" + rs.getInt("partnerId") + " - " + rs.getString("partnerName") + "</option>");
                                                    }
                                            %>
                                            <%=partnerData.toString()%>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Agent*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="agentid" class="txtbox"></option>
                                            <%
                                                StringBuffer agentData = new StringBuffer();
                                                query = "SELECT agentid,agentName FROM agents WHERE activation='T' ORDER BY agentid ASC";
                                                pstmt = conn.prepareStatement(query);
                                                rs = pstmt.executeQuery();
                                                while (rs.next())
                                                {
                                                    String selected = "";
                                                    if (rs.getString("agentid").equals(agentid))
                                                    {
                                                        selected = "selected";
                                                    }
                                                    agentData.append("<option value='" + rs.getInt("agentid") + "' " + selected + ">" + rs.getInt("agentid") + " - " + rs.getString("agentName") + "</option>");
                                                }
                                            %>
                                            <%=agentData.toString()%>
                                            <%
                                                }
                                                catch (SQLException e)
                                                {
                                                }
                                                catch (SystemError systemError)
                                                {
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>

                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Is Recurring</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="isrecurring" id="isrecurring">
                                            <%
                                                String isrecurring = request.getParameter("isrecurring");
                                                if ("1".equalsIgnoreCase(isrecurring))
                                                {
                                            %>
                                            <option value='0'>N</option>
                                            <option value='1' selected>Y</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value='0' selected>N</option>
                                            <option value='1'>Y</option>
                                            <% } %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">is Dynamic Descriptor</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="isDynamicDescriptor" id="isdynamicdescriptor">
                                            <%
                                                String isDynamicDescriptor= request.getParameter("isDynamicDescriptor");
                                                if ("N".equalsIgnoreCase(isDynamicDescriptor))
                                                {
                                            %>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%
                                            }
                                            else{
                                            %>
                                            <option value="N">N</option>
                                            <option value="Y" selected>Y</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">is 3D Supported</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="3dSupportAccount" id="3dSupportAccount">
                                            <%
                                                String supportaccount= request.getParameter("3dSupportAccount");
                                                if("Y".equalsIgnoreCase(supportaccount))
                                                {
                                            %>
                                            <option value="N">N</option>
                                            <option value="Y" selected>Y</option>
                                            <option value="O">O</option>
                                            <option value="R">R</option>
                                            <%
                                            } else if("O".equalsIgnoreCase(supportaccount))
                                            {
                                            %>
                                            <option value="N">N</option>
                                            <option value="Y">Y</option>
                                            <option value="O" selected>O</option>
                                            <option value="R">R</option>
                                            <%
                                            } else if("R".equalsIgnoreCase(supportaccount))
                                            {
                                            %>
                                            <option value="N">N</option>
                                            <option value="Y">Y</option>
                                            <option value="O">O</option>
                                            <option value="R" selected>R</option>
                                            <%
                                            }else
                                            {
                                            %>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <option value="O">O</option>
                                            <option value="R">R</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">3Ds Version</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="threeDsVersion" id="threeDsVersion">
                                            <%
                                                String threeDsVersion= request.getParameter("threeDsVersion");
                                                if ("3Dsv2".equalsIgnoreCase(threeDsVersion))
                                                {
                                            %>
                                            <option value="3Dsv2" selected>3Ds v2</option>
                                            <option value="3Dsv1">3Ds v1</option>
                                            <%
                                            }
                                            else{
                                            %>
                                            <option value="3Dsv1" selected>3Ds v1</option>
                                            <option value="3Dsv2">3Ds v2</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb"><%--From Account ID--%> Back Office User Name</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" maxlength="50" name="fromAccountId"
                                               id="fromAccountId" size="50"
                                               value="<%= request.getParameter("fromAccountId") != null?request.getParameter("fromAccountId"):"" %>">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb"><%--From MID--%> Back Office Password</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" maxlength="50" name="fromMid" id="fromMid"
                                               size="50"
                                               value="<%= request.getParameter("fromMid") != null?request.getParameter("fromMid"):"" %>">
                                    </td>
                                </tr>

                                <%
                                    if (dataStringBuilder.length() > 0)
                                    {
                                        GatewayType gatewayType = GatewayTypeService.getGatewayType(pgtypeid);
                                %>
                                <tr>
                                    <td>&nbsp;</td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb" colspan="4"
                                        align="center"><%=gatewayType.getGateway() %> Gateway Account Details
                                    </td>
                                </tr>
                                <%=dataStringBuilder.toString()%>
                                <%}%>
                                <tr>
                                    <td>&nbsp;</td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb"></td>
                                    <td style="padding: 3px" width="5%" class="textb"></td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <button type="submit" class="buttonform"
                                                value="Add Gateway Account"<%-- onclick="check();"--%>
                                                style="width:200px ">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;Add Gateway Account
                                        </button>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
</body>
</html>