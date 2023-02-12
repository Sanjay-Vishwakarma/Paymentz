<%--
  Created by IntelliJ IDEA.
  User: jignesh.r
  Date: Jun 8, 2006
  Time: 3:10:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
  <head>
      <title>Activate Merchant</title>
      <script type="text/javascript" language="javascript">

          function submitForm()
          {
              var frm = document.MerchantDetailsForm;
              if(isNaN(frm.merchantid.value))
                alert("Please Enter a numeric value for Merchant ID ")
              else
              frm.submit();
          }
      </script>
  </head>
  <body>
        <br><br>
        <center>
        <form action="Post" action="/servlet/ActivateMerchant" name = "MerchantDetailsForm">
            Merchant ID:    <input name="merchantid" type="text">

            <input type="button" onclick="submitForm()" value="Submit">
        </form>
        </center>
  </body>
</html>