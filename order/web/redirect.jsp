<%@ page import="sun.misc.BASE64Decoder" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/9/13
  Time: 1:46 AM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head/>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">
<br><br>
<br>

<%

    System.out.println("----------redirect.jsp called ----------------");
	for(Object key: request.getParameterMap().keySet()){
        System.out.println(key+"=");
        System.out.println(request.getParameter((String)key));
    }

%>
</body>
</html>