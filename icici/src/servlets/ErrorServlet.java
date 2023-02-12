import com.directi.pg.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;


public class ErrorServlet extends HttpServlet
{
    static Logger logger = new Logger(ErrorServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("inside ErrorServlet");

        try
        {
            PrintWriter pWriter = res.getWriter();
            res.setContentType("text/html");
            pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
            pWriter.println(URLDecoder.decode(req.getParameter("ERROR")));
            pWriter.flush();
        }
        catch (IOException ioe)
        {
            logger.error("IOException occure in ErrorServlet",ioe);
        }
        catch (Exception ex)
        {
            logger.error("Exception occure in ErrorServlet",ex);

        }
        logger.debug("leaving ErrorServlet");
    }

}