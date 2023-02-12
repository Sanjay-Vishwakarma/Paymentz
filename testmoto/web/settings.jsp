<%@ page import="java.util.ResourceBundle,
                 java.util.Enumeration,
                 com.directi.pg.LoadProperties,
                 com.logicboxes.util.Util,
                 com.logicboxes.util.ApplicationProperties,
                 java.io.FileOutputStream,
                 java.util.Properties,
                 java.io.FileInputStream,
                 org.apache.commons.cli.CommandLine,
                 java.io.InputStream,
                 sun.nio.cs.ext.IBM437,
                 java.net.URLDecoder"%>

<form name="settings" action="settings.jsp" method="POST">
<table>

<%
  try{


    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("settings.properties"); //new FileInputStream(application.getRealPath("settings.properties"));
    Properties props = new Properties();
    props.load(in);


    Enumeration eKeys = props.propertyNames();

    if (Util.parseData((String)request.getParameter("s")) != null || Util.parseData((String)request.getParameter("sr")) != null) {

        while(eKeys.hasMoreElements())

        {
            String key = (String)eKeys.nextElement();

            if(Util.parseData(request.getParameter(key+"_chk")) != null && Util.parseData(request.getParameter(key))!=null)
            {
                out.println(request.getParameter(key)+"="+URLDecoder.decode(request.getParameter(key)));
                props.setProperty(key,request.getParameter(key));
            }

        }

        try
        {
            FileOutputStream fout = new FileOutputStream(Thread.currentThread().getContextClassLoader().getResource("settings.properties").getPath());
            props.store(fout, "Updated by Configuration Screens");
            fout.flush();
            fout.close();
        }
        catch(Throwable e)
        {
            out.print(Util.getStackTrace(e));
        }
        
        if(Util.parseData((String)request.getParameter("sr")) != null)
        {
              Runtime rTime = Runtime.getRuntime();
              rTime.exec("D:\\Alpesh\\tomcat\\run.bat");
        }

    }

props.load(in);
Enumeration eKeys2 = props.propertyNames();

while(eKeys2.hasMoreElements())

 {

     String key = (String)eKeys2.nextElement();
     String value = props.getProperty(key);

%>
<tr>
<td><input type="checkbox" name="<%=key%>_chk" ></td><td><%=key%></td><td><input type="text" length="50" name="<%=key%>" value="<%=value%>"></td>
</tr>

<%

 }
  }
  catch(Throwable e)
  {
       out.print(Util.getStackTrace(e));

  }

%>
</table>
<input type="submit" name="s" value="Change Value">
<input type="submit" name="sr" value="Change Value & Restart Server">
<input type="reset" value="Reset Value">
</form>


