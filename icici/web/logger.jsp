<%@ page import="java.io.*" %> 
 
Log Message 
 
<% 
try 
{ 
	FileInputStream fi=new FileInputStream("/opt/tcold/log/log4j.log"); 
	DataInputStream din=new DataInputStream(fi); 
	String line=""; 
 
	while((line=din.readLine())!=null) 
	{ 
		out.println(line+"<br>"); 
	} 
	 
} 
catch(Exception ex) 
{ 
	out.println(ex.toString()); 
} 
 
%> 
