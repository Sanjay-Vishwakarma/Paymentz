
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 21, 2012
 * Time: 8:01:20 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class AGG extends HttpServlet
{
   public void doGet(HttpServletRequest req, HttpServletResponse res)
   {

   }
    public void doPost(HttpServletRequest req, HttpServletResponse res)
    {

        PrintWriter out=null;
        String ipaddr="1.2.3.4";
        String s = null;

        s="<?xml version=\"1.0\" standalone=\"yes\"?>" +
                "<XMLResponse>" +
                "<Authorize>" +
                "<AccountID>";
        s+= req.getParameter("ACCOUNTID") + "</AccountID><ProductCode>";
        s+=req.getParameter("PRODUCTCODE")+ "</ProductCode><RequestID>";
        s+=req.getParameter("REQUESTID") + "</RequestID><FirstName>";
        s+=req.getParameter("CARDHOLDER")+ "</FirstName><CardNumber>" +req.getParameter("PAN") + "</CardNumber>" +
                "<CVV>" +req.getParameter("ccid") +"</CVV>" +
                "<ExpDate>2009-01-01</ExpDate>";
        s+="<IPAddress>"+ ipaddr +"</IPAddress><RSASignature>" +
                "FDsEcT2bMYK8xhkf5o4Dv5BLbbij3T9_rb922z_zX919sBIqhLKSBOBZUwYgv9s3esD1Zsq68PWA" +
                "d3Tf5-rI5ooSp5AUott4QPYeQ3BO41j_3Mp6j_j3x7-JmHtlBUmT8-r3yWjK8TicGUM742ckOobJ" +
                "Og6jDEGkEBFv6EnrZNh6cRi3aPj0x7rS36FLPSeISK5pHvug9hDfaVUymnw7a8AP-2Bis478VVr_" +
                "6sOcF-VdkucpJ_i2AVqqbJ3_qaQXd0bB2CXsS46MpyQRfQZP92myZxBtZqOZYqlwEhJAob-LWoEk" +
                "iDtXznvQwfH0l0N4gYD_er28tmQM5ncQUEAMj1HO5WbwPG2pgEPDNpYiLLOWlTZlcOVpw0PFgWIF" +
                "i5urVoRcTBZRCgUwJELYlmopZV6Y8_3VH01b5diIGldrE3DT3n5oCHP5SkLXkcmdBXbNIwHmbfLN" +
                "B8PpnwXxvdIxFFjNYJe2WdCrubl1Ndi38VT3iNovq-91p59VXs2BsZdFGxu9SpPsGe-uiFK3e7LLzinyPuvHagHdjlBz0DxUMuW5u4_LhcTeK1wh0HEeY76EYanZEvXZAIzeIG6a3yHK39C7oxMik_i" +
                "wvuqCAn2zHR6hHjFv0dVAA38EIbLKJ1x4iNU8_Ic7UMtCsultdO1bYEabNPvpADnf9YQGFg7oUE," +
                "</RSASignature></Authorize>" +
                "</XMLRequest>";

        try
            {
                out = res.getWriter();

                URL u;
                u = new URL("https://netpaying.com/api/authorize");


                HttpURLConnection uc = (HttpURLConnection)u.openConnection();
                uc.setRequestMethod("POST");
                uc.setDoOutput(true);
                uc.setDoInput(true);
                uc.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
                uc.setAllowUserInteraction(false);
                DataOutputStream dstream = new DataOutputStream(uc.getOutputStream());
                dstream.writeBytes(s);
                dstream.close();
                InputStream in = uc.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuffer buf = new StringBuffer();
                String line;
                while ((line = r.readLine())!=null) {
                    buf.append(line);
                    }
                in.close();
                out.println(buf.toString());
            }
            catch (IOException e)
            {
                out.println(e.toString());
            }


    }


}
