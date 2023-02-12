<%--
  Created by IntelliJ IDEA.
  User: ThinkPadT410
  Date: 7/28/2016
  Time: 4:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title> Transaction</title>
</head>
<body>
<form action="https://check.safer2connect.com/servlet/NormalCustomerCheck" method="post" ENCTYPE="multipart/form-data" >
  <div class="row">
    <div class="col-lg-12">
      <div class="panel panel-default">
        <div class="panel-heading">
           Inquiry
        </div>
        <br>
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Merchant No</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text" value="" name="merNo">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Gateway No</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text" value="" name="gatewayNo">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Sign Info</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text" value="" name="singInfo">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Order No</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text" value="" name="orderNo">
                  </td>
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <button type="submit" class="buttonform">
                      Inquiry
                    </button>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</form>
</body>
</html>
