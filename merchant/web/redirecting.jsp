<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 11/20/14
  Time: 6:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
    <script src='/merchant/javascript/auto_submit.js'> </script>
        <script language="javascript">
        window.onload=function(){
            var counter = 5;
            var interval = setInterval(function() {
                counter = counter-1;
                $("#seconds").text(counter);

                if (counter === 0) {
                    clear();
                    redirect();
                    $("#seconds").text(0);
                }
            }, 1000);

            function clear()
            {
                clearInterval(interval);
            }

        };
        function redirect() {

            document.pay_form.target="_blank";
            document.pay_form.submit();
        };
    </script>
    <title></title>
</head>
<body class="bodybackground">
<%@ include file="Top.jsp" %>
<div class="row" >
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" style="background-color:#2c3e50;color:#ffffff;font-size:13px;">
                Redirecting Page
            </div>
            <div >
            <table align="center" width="100%" >
                <tr id="second" style="display: ''">
                    <td align="center" class="textb">You will be automatically redirected in <h4><b id="seconds">5</b></h4>seconds.</td>
                </tr>
                <tr>
                    <td><%=request.getAttribute("html")%></td>
                </tr>
            </table>
            </div>
        </div>
    </div>
</div>
        <%--</form>--%>

</body>
</html>