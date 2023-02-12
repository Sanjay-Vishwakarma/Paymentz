import com.directi.pg.Logger;
import com.directi.pg.Mail;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UnreserveResellerFundServlet extends HttpServlet
{

    private static Logger logger = new Logger(UnreserveResellerFundServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
    {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
    {
        logger.debug("Inside doPost of UnreserveResellerFund");
        ServletContext application = getServletContext();
        PrintWriter out = resp.getWriter();
        boolean status;
        String unreserveData = req.getParameter("unreservedata");  // I will get data in this format memberid:amount~memberid:amount
        String checkSum = req.getParameter("checksum");
        String key = (String) application.getAttribute("SFNB_KEY");



        StringBuffer unreservedIds = null;
        try
        {
            //Reseller r = new Reseller();
            //unreservedIds = r.unreserveFund(unreserveData);

            Mail.sendmail("admin@tc.com", "Do_Not_Reply@tc.com", "Unreserved was done successfully", "\r\n\r\n Below is result of unreserve.\r\n\r\n " + unreserveData + "\r\nRegards,\r\n Team");
        }
        catch (Exception e)
        {
            logger.error("Error while unreserving reseller amount. Data collected are",e);
        }

        logger.debug("Leaving doPost of UnreserveResellerFund");
        out.println(unreservedIds);
    }

}

/*

            url.append("?unreservedata=" + URLEncoder.encode(unreserveString.toString()));
            cat.debug("URL: " + url);
            con.setDoInput(true);
            cat.info("Connected to url, getting content");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer buf = new StringBuffer();
            int iter = 0;
            String s;
            while((s = in.readLine()) != null)
            {
                buf.append(s);
                if(iter++ > 5)
                {
                    cat.info("Breaking after 5 iterations");
                    break;
                }
            }
            String data = buf.toString();


*/