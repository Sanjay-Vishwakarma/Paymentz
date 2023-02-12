<%@ include file="ecoretab.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 6/12/13
  Time: 6:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<%
    session.setAttribute("submit","Manual Settlement");
%>
<div class="row" style="margin-top: 0px">
    <div class="col-lg-12">
        <div class="panel panel-default"style="margin-top: 0px">
            <div class="panel-heading" >
            Ecore Manual Settlement Transaction
            </div>

        <form action="/icici/ecoreManualCheckSettlement.jsp?ctoken=<%=ctoken%>" method="post"  >
            <input type="hidden" value="<%=ctoken%>" name="ctoken">
            <table  align="center" width="80%" cellpadding="2" cellspacing="2" >
                <tr>
                    <td>
                        <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                            <tr>
                                <td colspan="4">&nbsp;</td>
                            </tr>

                            <tr>
                                <td class="textb">&nbsp;</td>
                                <td  class="textb" >Tracking Id</td>
                                <td  class="textb"></td>
                                <td  class="textb">

                                    <input maxlength="15" type="text" name="trackingid" class="txtbox" value="">

                                </td>

                                <td  class="textb">&nbsp;</td>
                                <td  class="textb"></td>
                                <td  class="textb"></td>
                                <td  class="textb">
                                    <button type="submit" class="buttonform">
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;Process
                                    </button>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </form>

</div></div></div>

</body>
</html>