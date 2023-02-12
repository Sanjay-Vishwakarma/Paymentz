<%@ page language="java" import="java.sql.Timestamp,com.opus.epg.sfa.java.*,java.net.*,javax.net.*,
                                 java.util.Enumeration,
                                 com.sun.net.ssl.internal.www.protocol.https.Handler,
                                 java.security.Security,
                                 com.sun.net.ssl.internal.ssl.Provider,
                                 java.io.*,
                                 java.util.TimeZone,
                                 java.util.SimpleTimeZone" session="false" isErrorPage="false" %>
<html>
<head>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">
<title>SSL checkup</title>
</head>
<body>
<%

    System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
    Security.addProvider(new Provider());
    System.setProperty( "javax.net.debug" , "all" );
    out.println("Inside ssl.jsp new");
    System.out.println("Inside ssl.jsp new");
   URL url = new URL( "https://payseal.icicibank.com/mpi/Moto.jsp" );
    // URL url = new URL( "https://secure.payseal.com/MA" );
    URL url1 = new URL( "https" , url.getHost() , url.getPort() , url.getFile() , new Handler() );
//    for ( int i=1;i<5;i++ )
 //   {
    String ids = TimeZone.getTimeZone("GMT+5:30").getID();
                        SimpleTimeZone tz = new SimpleTimeZone(+0 * 00 * 60 * 1000, ids);
                        java.util.GregorianCalendar cal=new java.util.GregorianCalendar(tz);

        long start=System.currentTimeMillis();
        try
        {
            cal.setTime(new java.util.Date());

            out.println("<br>Start time: "+new java.util.Date()+"<br>");
            out.println("<br>opeing connection<br>");
           System.out.println("opeing connection<br>");
            HttpURLConnection httpurlconnection = ( HttpURLConnection ) url1.openConnection();
            httpurlconnection.setDoOutput( true );
            httpurlconnection.setUseCaches( false );
            out.println("1");
            System.out.println("1");
            httpurlconnection.setRequestProperty( "Content-Type" , "application/x-www-form-urlencoded" );
            httpurlconnection.setRequestProperty( "Accept" , "image/gif, image/x-xbitmap, image/jpeg,image/pjpeg, image/png, */*" );
            httpurlconnection.setRequestProperty( "Accept-Language" , "en" );
            out.println("2");
            System.out.println("2");
            out.flush();
            OutputStream o= httpurlconnection.getOutputStream();
            out.println("2.5");
            System.out.println("2.5");
            //out.flush();
            response.flushBuffer();
            DataOutputStream dataoutputstream = new DataOutputStream(o );
            out.println("3");
            System.out.println("3");
            dataoutputstream.writeBytes( "alpesh" );
            out.println("<br>opened....");
            System.out.println("<br>opened....");
            dataoutputstream.flush();
                dataoutputstream.close();
                dataoutputstream = null;
            //out.println("Security provider"+Security.getProperty("cert.provider.x509v1")+"<br>");
            out.println("<br>getting input stream ");
            System.out.println("<br>getting input stream ");
            StringBuffer stringbuffer;

            InputStream inputstream = httpurlconnection.getInputStream();
            System.out.println("<br>Got input stream ");
            out.println("<br>Got input stream ");
            InputStreamReader isr=new InputStreamReader( inputstream );

            System.out.println("<br>Got input stream reader");
            out.println("<br>Got input stream reader");

                BufferedReader bufferedreader = new BufferedReader( isr);

            System.out.println("<br>Got bufferreader");
            out.println("<br>Got bufferreader");

                stringbuffer = new StringBuffer();
                String s2;
            out.println("<br>Reading respnse");
            System.out.println("<br>Reading respnse");

            while ( ( s2 = bufferedreader.readLine() ) != null )
                    if ( s2.length() != 0 )
                        stringbuffer.append( s2.trim() );

            out.println("<br>closing buffer");
            System.out.println("<br>closing buffer");

            inputstream.close();
            inputstream=null;
                bufferedreader.close();
                bufferedreader = null;	 

            if ( httpurlconnection != null )
                httpurlconnection.disconnect();
        }
        catch ( IOException ioexception )
        {
            StringWriter sw=new StringWriter();
               PrintWriter pw=new PrintWriter(sw);
            ioexception.printStackTrace(pw);
            out.println( "IOException while opening connection : " + sw.toString());
            System.out.println( "IOException while opening connection : " + sw.toString());
            //throw new SFAApplicationException("Error while opening connection. Transaction cannot be processed");
        }
        catch(Throwable e)
        {
            StringWriter sw=new StringWriter();
               PrintWriter pw=new PrintWriter(sw);
            e.printStackTrace(pw);
            out.println( "Exception "+sw.toString());
        }
//    }
    out.println("<br>Completed...");
    System.out.println("Completed");
     cal.setTime(new java.util.Date());
    out.println("<br><br>End time: "+cal.getTime()+"<br>");
    out.println("<br>Time elapse: "+((System.currentTimeMillis()-start)/1000)+" sec. <br>");
    response.flushBuffer();

%>
</body>
</html>