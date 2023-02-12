<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.CustomerSupport" %>
<%@ include file="custSuppDash.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 4/15/14
  Time: 4:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%

%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title></title>
</head>
<body style="background-color: #ecf0f1">

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" style="background-color:#2c3e50;color:#ffffff;font-size:13px;  ">
                <p>
                    Customer Support Personal Details
                </p>
            </div><br><br>
            <form class="form-horizontal" role="form">
                <input type="hidden" name="ctoken" value="<%=ctoken%>">
                <table  align="center" width="50%" cellpadding="2" cellspacing="2">

                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" >Executive Name</td>
                                    <td width="10%" class="textb"><b>:</b></td>
                                    <td width="40%%">
                                        <input type="text" class="txtbox" readonly="true" style="font-size: 12px;width: 200px" value="<%=name%>">
                                    </td>
                                    </tr>
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" ></td>
                                    <td width="10%" class="textb"></td>
                                    <td width="40%%"></td>
                                  </tr>
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" ></td>
                                    <td width="10%" class="textb"></td>
                                    <td width="40%"></td>
                                </tr>
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb"  >Contact Number</td>
                                    <td width="10%" class="textb"><b>:</b></td>
                                    <td width="40%%">
                                        <input type="text" class="txtbox"  readonly="True" style="font-size: 12px;width: 200px" value="<%=ContactNo%>" size="40">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" ></td>
                                    <td width="10%" class="textb"></td>
                                    <td width="40%%"></td>
                                </tr>
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" ></td>
                                    <td width="10%" class="textb"></td>
                                    <td width="40%"></td>
                                </tr>


                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" >Email Address</td>
                                    <td width="10%" class="textb"><b>:</b></td>
                                    <td width="40%%">
                                        <input type="text" class="txtbox"  readonly="True" style="font-size: 12px;width: 200px" value="<%=Email%>" size="40">

                                    </td>
                                </tr>
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" ></td>
                                    <td width="10%" class="textb"></td>
                                    <td width="40%%"></td>
                                </tr>
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" ></td>
                                    <td width="10%" class="textb"></td>
                                    <td width="40%%"></td>
                                </tr>
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" >Join Date</td>
                                    <td width="10%" class="textb"><b>:</b></td>
                                    <td width="40%%">
                                        <input type="text" class="txtbox"  readonly="True" style="font-size: 12px;width: 200px" value="<%=JoinDate%>" size="40">
</td>
                                </tr>
                                </table>
                    </td>
                    </tr>
                    </table>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>