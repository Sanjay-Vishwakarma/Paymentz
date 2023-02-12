<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 6/24/14
  Time: 5:35 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="custSuppDash.jsp"%>
<html>
<head>
    <title></title>
</head>
<body>
<div class="row">
<div class="col-lg-12">
<div class="panel panel-default">
<div class="panel-heading" style="background-color:#2c3e50;color:#ffffff;font-size:13px;  ">
    <p> Caller Entry</p>
</div><br><br>
<%
    String b = (String)request.getAttribute("update");
    String nopodbatch= (String) request.getAttribute("nopodbatch");
    String mes=request.getParameter("MES");
    Hashtable errorM=null;
    Hashtable errorO= null;
    if(mes!=null)
    {
        errorM=(Hashtable) request.getAttribute("errorM");
        errorO= (Hashtable) request.getAttribute("errorO");
        if(errorM!=null && !errorM.isEmpty() )
        {

            Enumeration enuM = errorM.keys();
            String keyM = "";
            String valueM = "";
            while (enuM.hasMoreElements())
            {
                keyM = (String) enuM.nextElement();
                valueM = (String) errorM.get(keyM);
                out.println("<center><li><font class=\"textb\"><b>"+valueM+"</b></font></li></center>");
            }
        }
        if(errorO!=null && !errorO.isEmpty())
        {

            Enumeration enuO = errorO.keys();
            String keyO = "";
            String valueO = "";
            while (enuO.hasMoreElements())
            {
                keyO = (String) enuO.nextElement();
                valueO = (String) errorO.get(keyO);
                out.println("<center><li><font class=\"textb\"><b>"+valueO+"</b></font></li></center>");
            }
        }
    }

    if("yes".equals(nopodbatch))
    {
        out.println("<font class=\"textb\"><b>PLEASE ENTER PODBATCH WITH CORRESPONDING POD</b></font>");
    }
    if("no".equals(nopodbatch))
    {
        out.println("<font class=\"textb\"><b>PLEASE ENTER POD WITH CORRESPONDING PODBATCH</b></font>");
    }
    if("true".equals(b))
    {
        out.println("<center><li><fontclass=\"textb\"><b>New Caller added</b></font></li></center>");
    }
%>
<form id="form1" class="form-horizontal"  action="/support/servlet/Caller?ctoken=<%=ctoken%>" method="post" autocomplete="off">
<input type="hidden" value="<%=ctoken%>" name="ctoken">
<input type="hidden" value="newCaller" name="page">
<table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:5%; ">

    <tr>
        <td>

            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                <tr>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>

                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>
                </tr>
                <tr>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >Tracking Id</td>
                    <td width="14px" class="textb"></td>
                    <td width="155px" class="textb">
                        <input type="text" class="txtbox" readonly="true" size="20" name="STrakingid" value="" placeholder="NOT SPECIFIED" style="font-size: 12px">
                    </td>
                </tr>

                <tr>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>

                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>
                </tr>

                <tr>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >First Name</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="155px" class="textb">
                        <input type="text" class="txtbox"  size="20" required="true" name="firstname"  style="font-size: 12px">
                    </td>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >Last Name</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="155px" class="textb">
                        <input type="text" class="txtbox"  size="20" required="true" name="lastname"  style="font-size: 12px">
                    </td>
                </tr>
                <tr>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>

                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>
                </tr>
                <tr>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >Email Address</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="155px" class="textb">
                        <input type="text" class="txtbox"  size="20" required="true" name="email"  style="font-size: 12px">
                    </td>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >Phone No</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="155px" class="textb">
                        <input type="text" class="txtbox"  size="20" name="phoneno"  style="font-size: 12px" maxlength="15">
                    </td>
                </tr>
                <tr>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>

                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>
                </tr>
                <tr>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >Shipping Id</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="155px" class="textb">
                        <input type="text" class="txtbox"  size="20"  name="Shippingid"  style="font-size: 12px">
                    </td>

                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >Shipping Site</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="155px" class="textb">
                        <input type="text" class="txtbox"  size="20"  name="Shippingbno"  style="font-size: 12px">
                    </td>
                </tr>
                <tr>
                <tr>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>

                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>
                </tr>
                <tr>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>

                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>
                </tr>
            </table>
        </td>
    </tr>
</table>
<table  align="center" width="950px" cellpadding="2" cellspacing="2" style="margin-left:80px; ">
    <tr>
        <td>
            <table border="0" cellpadding="5" cellspacing="0" width="950px"  align="center">
                <tr style="height: 51px">
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >Description</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="155px" class="textb">
                        <textarea class="txtbox"  name="desc" value="" maxlength="255"  style="font-size: 12px;max-height: 50px;max-width: 150px"></textarea>
                    </td>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >Remark</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="155px" class="textb">
                        <textarea type="textarea" class="txtbox"  name="remark" value="" maxlength="255"  style="font-size: 12px;max-height: 50px;max-width: 150px"></textarea>
                    </td>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >Status</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="155px" class="textb">
                        <textarea  type="textarea" class="txtbox" maxlength="255"  name="status" value=""  style="font-size: 12px;max-height: 50px;max-width: 150px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>

                    <td width="6px" class="textb">&nbsp;</td>
                    <td width="66px" class="textb" >&nbsp;</td>
                    <td width="14px" class="textb">&nbsp;</td>
                    <td width="141px" class="textb">&nbsp;</td>
                </tr>
            </table>
        </td>
    </tr>
</table>
<table>

    <tr>
        <td width="6px" class="textb">&nbsp;</td>
        <td width="66px" class="textb" >&nbsp;</td>
        <td width="14px" class="textb">&nbsp;</td>
        <td width="141px" class="textb">&nbsp;</td>

        <td width="6px" class="textb">&nbsp;</td>
        <td width="66px" class="textb" >&nbsp;</td>
        <td width="14px" class="textb">&nbsp;</td>
        <td width="141px" class="textb">&nbsp;</td>

        <td width="6px" class="textb">&nbsp;</td>
        <td width="66px" class="textb" >&nbsp;</td>
        <td width="14px" class="textb">&nbsp;</td>
        <td width="100px" class="textb">&nbsp;</td>

        <td width="6px" class="textb">&nbsp;</td>
        <td width="66px" class="textb" >&nbsp;</td>
        <td width="14px" class="textb">&nbsp;</td>
        <td width="141px" class="textb">&nbsp;</td>

        <td width="6px" class="textb">&nbsp;</td>
        <td width="66px" class="textb" >&nbsp;</td>
        <td width="14px" class="textb">&nbsp;</td>
        <td width="141px" class="textb">&nbsp;</td>

        <td width="6px" class="textb">&nbsp;</td>
        <td width="66px" class="textb" >
            <button type="submit" class="buttonform" name="B1">
                    <span>
                        <i class="fa fa-clock-o" style="float: left"></i>
                    </span>
                Save  </button></td>
        <td width="14px" class="textb">&nbsp;</td>
        <td width="141px" class="textb">&nbsp;</td>
    </tr>    </table>
</form> </div> </div></div></center>

</body>
</html>