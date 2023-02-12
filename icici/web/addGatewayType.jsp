<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: mukesh.a
  Date: 1/23/14
  Time: 12:52 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    Logger logger=new Logger("addGatewayType.jsp");
%>
<html>
<head><title>Add New Gateway Type</title>
    <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js"></script>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script type="text/javascript">
        function check()
        {
            var msg = "" ;
            var flag = "false";
            if (document.getElementById("gateway").value.length == 0 || document.getElementById("gateway").value.length > 10)
            {
                msg = msg + "\nPlease enter Gateway/Gateway values should be less than 10 character";
                document.getElementById("gateway").focus();
                return;
            }

//            if (document.getElementById("gateway").value.length > 0)
//            {
//                var regex = "/^([0-9]|[a-z])+([0-9a-z]+)$/i";
//                var gateway = document.getElementById("gateway").value;
//                if(gateway.match(regex))
//                {
//                    msg = msg + "\nPlease enter only alpha numeric values.";
//                    alert(msg);
//                    document.getElementById("gateway").focus();
//                    return;
//                }
//            }

//            if (document.getElementById("gateway").value.length > 10 )
//            {
//                msg = msg + "\nGateway values had be less then 10 character.";
//                alert(msg);
//                document.getElementById("gateway").focus();
//                return;
//            }

            if (document.getElementById("currency").value.length == 0 || document.getElementById("currency").value.length > 3)
            {
                msg = msg + "\nPlease enter currency/currency can be greater than 3 character.";
                alert(msg);
                document.getElementById("currency").focus();
                return;
            }

//            if (document.getElementById("currency").value.length > 3)
//            {
//                msg = msg + "\nCurrencty cannot be greater then 3 character.";
//                alert(msg);
//                document.getElementById("currency").focus();
//                return;
//            }

            if (document.getElementById("name").value.length == 0)
            {
                msg = msg + "\nPlease enter gateway name.";
                alert(msg);
                document.getElementById("name").focus();
                return;
            }

//            if (document.getElementById("name").value.length > 0)
//            {
//                var regex = "/^([0-9]|[a-z])+([0-9a-z]+)$/i";
//                var name = document.getElementById("name").value;
//                if(!name.match(regex))
//                {
//                    msg = msg + "\nPlease enter only alpha numeric values.";
//                    alert(msg);
//                    document.getElementById("name").focus();
//                    return;
//                }
//            }

            document.addtype.submit();
        }
    </script>

</head>
<body>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Add GateWay Type
                <div style="float: right;">
                    <form action="/icici/gatewayInterface.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New Gateway" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Gateway Master
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/addBankPartnerMapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add Bank Partner Mapping" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Bank Partner Mapping
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/addBankAgentMapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add Bank Agent Mapping" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Bank Agent Mapping
                        </button>
                    </form>
                </div>
            </div>
            <%
                ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                if (!com.directi.pg.Admin.isLoggedIn(session))
                {
                    response.sendRedirect("/icici/logout.jsp");
                    return;
                }
            %>
            <%
                String gateway,currency,name,address,gatewaytable,message,pspcode,key,wsservice,wspassword,country;
                gateway = currency = name = address = gatewaytable = pspcode = key = wsservice = wspassword = country= "";
                Integer chargepercentage,taxpercentage,withdrawalcharge,reversalcharge,chargebackcharge,chargesaccount,taxaccount,highriskamount ;
                chargepercentage = taxpercentage = withdrawalcharge = reversalcharge = chargebackcharge = chargesaccount = taxaccount = highriskamount = 0;
                String timedifferencenormal,timedifferencedaylight;
                timedifferencenormal = timedifferencedaylight =  "00:00:00";

                String bankip = "";
                ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

                //String isSubmitted =  request.getParameter("errormsg");
                if(request.getAttribute("success")!=null)
                {
                    out.println("<center><font class=\"textb\">" +request.getAttribute("success")+ "</font></center>");
                }
                if(request.getAttribute("error")!=null)
                {
                    out.println("<center><font class=\"textb\">" +request.getAttribute("error")+ "</font></center>");
                }
                if(request.getAttribute("errormsg")!=null)
                {
                    ValidationErrorList error= (ValidationErrorList) request.getAttribute("errormsg");
                    for(Object errorList : error.errors())
                    {
                        ValidationException ve = (ValidationException) errorList;
                        out.println("<center><font class=\"textb\">" + ve.getMessage() + "</font></center>");
                    }

                }

            %>
            <form name="addtype" action="/icici/servlet/addGatewayType?ctoken=<%=ctoken%>" method="post" name="form1" onsubmit="return check();">
                <input type="hidden" value="<%=ctoken%>" id="ctoken" name="ctoken">
                <input type="hidden" value="true" name="isSubmitted">
                <table align="center" width="65%" cellpadding="2" cellspacing="2">
                    <tbody>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                <tr><td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb">Gateway *(max 10 characters)</td>
                                    <td style="padding: 3px" width="5%" class="textb">:</td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <input class="txtbox" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(gateway)%>" name="gateway" size="50" id="gateway">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Name*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" name="name" size="50" id="name"></td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Currency *(3 characters)</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(currency)%>" name="currency" size="10" <%--id="currency"--%>></td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Is Cvv Optional*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="isCvvOptional" id="isCvvOptional">
                                            <option value='N' selected >N</option>
                                            <option value='Y'>Y</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Charge Percentage</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text"  value="<%=chargepercentage%>" name="chargepercentage" size="10" id="chargepercentage"></td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Tax Percentage</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" value="<%=taxpercentage%>" name="taxpercentage" id="taxpercentage" size="10"></td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">WithDraw Charge</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" value="<%=withdrawalcharge%>" name="withdrawalcharge" id="withdrawalcharge" size="10"></td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Reversal Charge</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" value="<%=reversalcharge%>" name="reversalcharge" id="reversalcharge" size="10"></td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">ChargeBackCharges </td>
                                    <td style="padding: 3px" class="textb" valign="top">:</td>
                                    <td style="padding: 3px" valign="top">
                                        <input class="txtbox" type="Text" value="<%=chargebackcharge%>" name="chargebackcharge" id="chargebackcharge" size="10">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Charges Account </td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" value="<%=chargesaccount%>" name="chargesaccount" id="chargesaccount" size="10">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Tax Account</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" value="<%=taxaccount%>" name="taxaccount" id = "taxaccount" size="30">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">HighRisk Amount</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" type="Text" value="<%=highriskamount%>" name="highriskamount" id="highriskamount" size="30">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Address</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <textarea name="address" id="address" maxlength="100" value="<%=address%>" rows="5" class="txtbox" cols="30"></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Gateway Table Name </td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <textarea name="gatewaytablename" id="gatewaytable" maxlength="100" value="<%=gatewaytable%>" rows="5" class="txtbox" cols="30"></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Time Difference Normal</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" value="<%=timedifferencenormal%>" name="timedifferencenormal" id="timedifferencenormal" size="25">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Time Difference Day Light</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" value="<%=timedifferencedaylight%>" name="timedifferencedaylight" id="timedifferencedaylight" size="25">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Partner*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input name="partnerid" class="txtbox" id="allpid" autocomplete="on">
                                        <%--<select name="partnerid" class="txtbox">
                                            <option value=""></option>
                                            <%
                                                Connection conn=null;

                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                                                    PreparedStatement pstmt = conn.prepareStatement(query);
                                                    ResultSet rs = pstmt.executeQuery();



                                                    while (rs.next())
                                                    {
                                                        //System.out.println(rs.getInt("partnerId"));
                                                        String selected = "";
                                                        if(rs.getInt("partnerId")==1)
                                                        {
                                                            selected = "selected";
                                                        }
                                                        out.println("<option value=\"" + rs.getInt("partnerId") + "\" " + selected + ">" + rs.getInt("partnerId") + " - " + rs.getString("partnerName") + "</option>");
                                                    }
                                                }
                                                catch(Exception e)
                                                {
                                                    logger.error("Exception:::::"+e);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>--%>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Agent*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input name="agentid" class="txtbox" id="agnt" autocomplete="on">

                                    <%--<select name="agentid" class="txtbox"></option>
                                            <option value=""></option>
                                            <%
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String query = "SELECT agentid,agentName FROM agents WHERE activation='T' ORDER BY agentid ASC";
                                                    PreparedStatement pstmt = conn.prepareStatement(query);
                                                    ResultSet rs = pstmt.executeQuery();

                                                    while (rs.next())
                                                    {
                                                        String selected = "";
                                                        if(rs.getInt("agentid")==1)
                                                        {
                                                            selected = "selected";
                                                        }
                                                        out.println("<option value=\"" + rs.getInt("agentid") + "\" " + selected + ">" + rs.getInt("agentid") + " - " + rs.getString("agentName") + "</option>");
                                                    }
                                                }
                                                catch(Exception e)
                                                {
                                                    logger.error("Exception:::"+e);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>--%>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Bank IP Address</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" value="<%=bankip%>" name="bankip" id="bankip" size="25">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">PSP Code</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" value="<%=pspcode%>" name="pspcode" id="pspcode" size="25" maxlength="255">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Key</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" value="<%=key%>" name="key" id="key" size="25" maxlength="255">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">WS Service</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" value="<%=wsservice%>" name="wsservice" id="wsservice" size="25" maxlength="255">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">WS Password</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <input class="txtbox" type="Text" value="<%=wspassword%>" name="wspassword" id="wspassword" size="25" maxlength="255">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Gateway Country*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" class="textb">
                                        <select name="country" placeholder="Country*" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>"  onchange="myjunk();"  >
                                            <option value="">Select a Country</option>
                                            <option value="AF|093">Afghanistan</option>
                                            <option value="AX|358">Aland Islands</option>
                                            <option value="AL|355">Albania</option>
                                            <option value="DZ|231">Algeria</option>
                                            <option value="AS|684">American Samoa</option>
                                            <option value="AD|376">Andorra</option>
                                            <option value="AO|244">Angola</option>
                                            <option value="AI|001">Anguilla</option>
                                            <option value="AQ|000">Antarctica</option>
                                            <option value="AG|001">Antigua and Barbuda</option>
                                            <option value="AR|054">Argentina</option>
                                            <option value="AM|374">Armenia</option>
                                            <option value="AW|297">Aruba</option>
                                            <option value="AU|061">Australia</option>
                                            <option value="AT|043">Austria</option>
                                            <option value="AZ|994">Azerbaijan</option>
                                            <option value="BS|001">Bahamas</option>
                                            <option value="BH|973">Bahrain</option>
                                            <option value="BD|880">Bangladesh</option>
                                            <option value="BB|001">Barbados</option>
                                            <option value="BY|375">Belarus</option>
                                            <option value="BE|032">Belgium</option>
                                            <option value="BZ|501">Belize</option>
                                            <option value="BJ|229">Benin</option>
                                            <option value="BM|001">Bermuda</option>
                                            <option value="BT|975">Bhutan</option>
                                            <option value="BO|591">Bolivia</option>
                                            <option value="BA|387">Bosnia and Herzegovina</option>
                                            <option value="BW|267">Botswana</option>
                                            <option value="BV|000">Bouvet Island</option>
                                            <option value="BR|055">Brazil</option>
                                            <option value="IO|246">British Indian Ocean Territory</option>
                                            <option value="VG|001">British Virgin Islands</option>
                                            <option value="BN|673">Brunei</option>
                                            <option value="BG|359">Bulgaria</option>
                                            <option value="BF|226">Burkina Faso</option>
                                            <option value="BI|257">Burundi</option>
                                            <option value="KH|855">Cambodia</option>
                                            <option value="CM|237">Cameroon</option>
                                            <option value="CA|001">Canada</option>
                                            <option value="CV|238">Cape Verde</option>
                                            <option value="KY|001">Cayman Islands</option>
                                            <option value="CF|236">Central African Republic</option>
                                            <option value="TD|235">Chad</option>
                                            <option value="CL|056">Chile</option>
                                            <option value="CN|086">China</option>
                                            <option value="CX|061">Christmas Island</option>
                                            <option value="CC|061">Cocos (Keeling) Islands</option>
                                            <option value="CO|057">Colombia</option>
                                            <option value="KM|269">Comoros</option>
                                            <option value="CK|682">Cook Islands</option>
                                            <option value="CR|506">Costa Rica</option>
                                            <option value="CI|225">Cote d'Ivoire</option>
                                            <option value="HR|385">Croatia</option>
                                            <option value="CU|053">Cuba</option>
                                            <option value="CW|599">Curacao</option>
                                            <option value="CY|357">Cyprus</option>
                                            <option value="CZ|420">Czech Republic</option>
                                            <option value="CD|243">Democratic Republic of the Congo</option>
                                            <option value="DK|045">Denmark</option>
                                            <option value="DJ|253">Djibouti</option>
                                            <option value="DM|001">Dominica</option>
                                            <option value="DO|001">Dominican Republic</option>
                                            <option value="EC|593">Ecuador</option>
                                            <option value="EG|020">Egypt</option>
                                            <option value="SV|503">El Salvador</option>
                                            <option value="GQ|240">Equatorial Guinea</option>
                                            <option value="ER|291">Eritrea</option>
                                            <option value="EE|372">Estonia</option>
                                            <option value="ET|251">Ethiopia</option>
                                            <option value="FK|500">Falkland Islands</option>
                                            <option value="FO|298">Faroe Islands</option>
                                            <option value="FJ|679">Fiji</option>
                                            <option value="FI|358">Finland</option>
                                            <option value="FR|033">France</option>
                                            <option value="GF|594">French Guiana</option>
                                            <option value="PF|689">French Polynesia</option>
                                            <option value="TF|000">French Southern and Antarctic Lands</option>
                                            <option value="GA|241">Gabon</option>
                                            <option value="GM|220">Gambia</option>
                                            <option value="GE|995">Georgia</option>
                                            <option value="DE|049">Germany</option>
                                            <option value="GH|233">Ghana</option>
                                            <option value="GI|350">Gibraltar</option>
                                            <option value="GR|030">Greece</option>
                                            <option value="GL|299">Greenland</option>
                                            <option value="GD|001">Grenada</option>
                                            <option value="GP|590">Guadeloupe</option>
                                            <option value="GU|001">Guam</option>
                                            <option value="GT|502">Guatemala</option>
                                            <option value="GG|000">Guernsey</option>
                                            <option value="GN|224">Guinea</option>
                                            <option value="GW|245">Guinea-Bissau</option>
                                            <option value="GY|592">Guyana</option>
                                            <option value="HT|509">Haiti</option>
                                            <option value="HM|672">Heard Island & McDonald Islands</option>
                                            <option value="HN|504">Honduras</option>
                                            <option value="HK|852">Hong Kong</option>
                                            <option value="HU|036">Hungary</option>
                                            <option value="IS|354">Iceland</option>
                                            <option value="IN|091">India</option>
                                            <option value="ID|062">Indonesia</option>
                                            <option value="IR|098">Iran</option>
                                            <option value="IQ|964">Iraq</option>
                                            <option value="IE|353">Ireland</option>
                                            <option value="IL|972">Israel</option>
                                            <option value="IT|039">Italy</option>
                                            <option value="JM|001">Jamaica</option>
                                            <option value="JP|081">Japan</option>
                                            <option value="JE|044">Jersey</option>
                                            <option value="JO|962">Jordan</option>
                                            <option value="KZ|007">Kazakhstan</option>
                                            <option value="KE|254">Kenya</option>
                                            <option value="KI|686">Kiribati</option>
                                            <option value="KW|965">Kuwait</option>
                                            <option value="KG|996">Kyrgyzstan</option>
                                            <option value="LA|856">Laos</option>
                                            <option value="LV|371">Latvia</option>
                                            <option value="LB|961">Lebanon</option>
                                            <option value="LS|266">Lesotho</option>
                                            <option value="LR|231">Liberia</option>
                                            <option value="LY|218">Libya</option>
                                            <option value="LI|423">Liechtenstein</option>
                                            <option value="LT|370">Lithuania</option>
                                            <option value="LU|352">Luxembourg</option>
                                            <option value="MO|853">Macau, China</option>
                                            <option value="MK|389">Macedonia</option>
                                            <option value="MG|261">Madagascar</option>
                                            <option value="MW|265">Malawi</option>
                                            <option value="MY|060">Malaysia</option>
                                            <option value="MV|960">Maldives</option>
                                            <option value="ML|223">Mali</option>
                                            <option value="MT|356">Malta</option>
                                            <option value="MH|692">Marshall Islands</option>
                                            <option value="MQ|596">Martinique</option>
                                            <option value="MR|222">Mauritania</option>
                                            <option value="MU|230">Mauritius</option>
                                            <option value="YT|269">Mayotte</option>
                                            <option value="MX|052">Mexico</option>
                                            <option value="FM|691">Micronesia, Federated States of</option>
                                            <option value="MD|373">Moldova</option>
                                            <option value="MC|377">Monaco</option>
                                            <option value="MN|976">Mongolia</option>
                                            <option value="ME|382">Montenegro</option>
                                            <option value="MS|001">Montserrat</option>
                                            <option value="MA|212">Morocco</option>
                                            <option value="MZ|258">Mozambique</option>
                                            <option value="MM|095">Myanmar</option>
                                            <option value="NA|264">Namibia</option>
                                            <option value="NR|674">Nauru</option>
                                            <option value="NP|977">Nepal</option>
                                            <option value="AN|599">Netherlands Antilles</option>
                                            <option value="NL|031">Netherlands</option>
                                            <option value="NC|687">New Caledonia</option>
                                            <option value="NZ|064">New Zealand</option>
                                            <option value="NI|505">Nicaragua</option>
                                            <option value="NE|227">Niger</option>
                                            <option value="NG|234">Nigeria</option>
                                            <option value="NU|683">Niue</option>
                                            <option value="NF|672">Norfolk Island</option>
                                            <option value="KP|850">North Korea</option>
                                            <option value="MP|001">Northern Mariana Islands</option>
                                            <option value="NO|047">Norway</option>
                                            <option value="OM|968">Oman</option>
                                            <option value="PK|092">Pakistan</option>
                                            <option value="PW|680">Palau</option>
                                            <option value="PS|970">Palestinian Authority</option>
                                            <option value="PA|507">Panama</option>
                                            <option value="PG|675">Papua New Guinea</option>
                                            <option value="PY|595">Paraguay</option>
                                            <option value="PE|051">Peru</option>
                                            <option value="PH|063">Philippines</option>
                                            <option value="PN|064">Pitcairn Islands</option>
                                            <option value="PL|048">Poland</option>
                                            <option value="PT|351">Portugal</option>
                                            <option value="PR|001">Puerto Rico</option>
                                            <option value="QA|974">Qatar</option>
                                            <option value="CG|242">Republic of the Congo</option>
                                            <option value="RE|262">Reunion</option>
                                            <option value="RO|040">Romania</option>
                                            <option value="RU|007">Russia</option>
                                            <option value="RW|250">Rwanda</option>
                                            <option value="BL|590">Saint Barthelemy</option>
                                            <option value="SH|290">Saint Helena, Ascension & Tristan daCunha</option>
                                            <option value="KN|001">Saint Kitts and Nevis</option>
                                            <option value="LC|001">Saint Lucia</option>
                                            <option value="MF|590">Saint Martin</option>
                                            <option value="PM|508">Saint Pierre and Miquelon</option>
                                            <option value="VC|001">Saint Vincent and Grenadines</option>
                                            <option value="WS|685">Samoa</option>
                                            <option value="SM|378">San Marino</option>
                                            <option value="ST|239">Sao Tome and Principe</option>
                                            <option value="SA|966">Saudi Arabia</option>
                                            <option value="SN|221">Senegal</option>
                                            <option value="RS|381">Serbia</option>
                                            <option value="SC|248">Seychelles</option>
                                            <option value="SL|232">Sierra Leone</option>
                                            <option value="SG|065">Singapore</option>
                                            <option value="SK|421">Slovakia</option>
                                            <option value="SI|386">Slovenia</option>
                                            <option value="SB|677">Solomon Islands</option>
                                            <option value="SO|252">Somalia</option>
                                            <option value="ZA|027">South Africa</option>
                                            <option value="GS|000">South Georgia & South Sandwich Islands</option>
                                            <option value="KR|082">South Korea</option>
                                            <option value="ES|034">Spain</option>
                                            <option value="LK|094">Sri Lanka</option>
                                            <option value="SD|249">Sudan</option>
                                            <option value="SR|597">Suriname</option>
                                            <option value="SJ|047">Svalbard and Jan Mayen</option>
                                            <option value="SZ|268">Swaziland</option>
                                            <option value="SE|046">Sweden</option>
                                            <option value="CH|041">Switzerland</option>
                                            <option value="SY|963">Syria</option>
                                            <option value="TW|886">Taiwan</option>
                                            <option value="TJ|992">Tajikistan</option>
                                            <option value="TZ|255">Tanzania</option>
                                            <option value="TH|066">Thailand</option>
                                            <option value="TL|670">Timor-Leste</option>
                                            <option value="TG|228">Togo</option>
                                            <option value="TK|690">Tokelau</option>
                                            <option value="TO|676">Tonga</option>
                                            <option value="TT|001">Trinidad and Tobago</option>
                                            <option value="TN|216">Tunisia</option>
                                            <option value="TR|090">Turkey</option>
                                            <option value="TM|993">Turkmenistan</option>
                                            <option value="TC|001">Turks and Caicos Islands</option>
                                            <option value="TV|688">Tuvalu</option>
                                            <option value="UG|256">Uganda</option>
                                            <option value="UA|380">Ukraine</option>
                                            <option value="AE|971">United Arab Emirates</option>
                                            <option value="GB|044">United Kingdom</option>
                                            <option value="US|001">United States</option>
                                            <option value="VI|001">United States Virgin Islands</option>
                                            <option value="UY|598">Uruguay</option>
                                            <option value="UZ|998">Uzbekistan</option>
                                            <option value="VU|678">Vanuatu</option>
                                            <option value="VA|379">Vatican City</option>
                                            <option value="VE|058">Venezuela</option>
                                            <option value="VN|084">Vietnam</option>
                                            <option value="WF|681">Wallis and Futuna</option>
                                            <option value="EH|212">Western Sahara</option>
                                            <option value="YE|967">Yemen</option>
                                            <option value="ZM|260">Zambia</option>
                                            <option value="ZW|263">Zimbabwe</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb"></td>
                                    <td style="padding: 3px" width="5%" class="textb"></td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <button type="submit" class="buttonform" value="Add Gateway Type" onclick="check();" style="width:200px ">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;Add Gateway Type
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

        </div>
    </div>
</div>
</body>
</html>