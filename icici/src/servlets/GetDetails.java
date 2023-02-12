import com.directi.pg.Transaction;
import com.directi.pg.LoadProperties;
import com.logicboxes.util.Util;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Mar 14, 2007
 * Time: 7:33:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetDetails extends HttpServlet
{   final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Servlet");
    final  String PROXYHOST = RB.getString("PROXYHOST");
        final  String PROXYPORT = RB.getString("PROXYPORT");
        final String PROXYSCHEME = RB.getString("PROXYSCHEME");


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ServletContext ctx = this.getServletContext();
        Transaction transaction = new Transaction();
        String trackingId = request.getParameter("trackingid");
        String memberId = request.getParameter("id");
        String newChecksum = request.getParameter("newchecksum");
        String version = request.getParameter("version");
        String redirectUrl = request.getParameter("redirecturl");
        Hashtable<String, String>  paramHash = new Hashtable<String, String>();
        Map<String, String[]> paramMap = (Map<String, String[]>) request.getParameterMap();
        for (Map.Entry<String, String[]> e : paramMap.entrySet())
        {
            String key = e.getKey();
            String[] values = e.getValue();
            paramHash.put(key, values[0]);
        }
        ctx.log("Param hash in GetDetailsServlet : " + paramHash.toString());
        PrintWriter out = response.getWriter();
        ctx.log("Tracking Id : " + trackingId);
        Hashtable<String, String> details = null;
        try
        {
            details = transaction.getDetails(trackingId, memberId, newChecksum, version, redirectUrl);
            details.putAll(paramHash);
        }
        catch (Exception e)
        {
            ctx.log("Errror .." + (Util.getStackTrace(e)));
            throw new ServletException(e);
        }
        request.setAttribute("details", details);
        RequestDispatcher view = request.getRequestDispatcher(""+PROXYSCHEME + "://" + PROXYHOST + ":" + PROXYPORT + "/icici/getDetails.jsp");
        view.forward(request, response);
    }

}
