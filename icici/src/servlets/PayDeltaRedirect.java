
import com.directi.pg.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 24/8/13
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayDeltaRedirect  extends TcServlet
{
    static Logger logger = new Logger(PayDeltaRedirect.class.getName());

    public PayDeltaRedirect()
    {
        super();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("ENTERED IN RedirectURL");

        PrintWriter pWriter = res.getWriter();
        res.setContentType("text");
        // pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        pWriter.println("OK");
        res.flushBuffer();

        /*System.out.println("Requeset PathInfo=>" + request.getPathInfo());
        System.out.println("Requeset PathTranslated=>"+ request.getPathTranslated());
        System.out.println("Requeset RequestURI=>" + request.getRequestURI());
        System.out.println("Requeset QueryString=>"+ request.getQueryString());*/
        Enumeration enumeration = request.getParameterNames();
        String[] paramValues = null;
        String paramName = null;
        StringBuffer buffer = new StringBuffer();
        int paramValuesSize = 0;

        while (enumeration != null && enumeration.hasMoreElements()) {
            buffer.delete(0, buffer.length());
            paramName = (String) enumeration.nextElement();
            paramValues = request.getParameterValues(paramName);
            paramValuesSize = 0;
            if (paramValues != null && paramValues.length > 0)
                paramValuesSize = paramValues.length;

            for (int i = 0; i < paramValuesSize; i++) {
                buffer.append(paramValues[i] + ",");
            }

            buffer.deleteCharAt(buffer.lastIndexOf(","));

            //System.out.println("Requeset Parameter NAME=>" + paramName + " VALUES =>" + buffer.toString());
            //logger.debug("Requeset Parameter NAME=>" + paramName + " VALUES =>" + buffer.toString());
        }

        //System.out.println("Request Charater Encoding is ==>"+ request.getCharacterEncoding());



    }

}
