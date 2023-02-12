<%@ page isErrorPage="true" import="java.lang.*,
                                    com.directi.pg.Functions" %>
<html>
<head>
</head><body LINK="#0000ff" VLINK="#800080">

<% 
	if(exception!=null)
	{
	String mess = exception.toString();
          
    out.println(Functions.ShowMessage("Error", mess));

	}
%>
</body>
</html>
