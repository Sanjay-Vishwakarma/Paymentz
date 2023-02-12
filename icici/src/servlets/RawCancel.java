import com.directi.pg.Functions;
import com.directi.pg.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.Statement;

public class RawCancel extends HttpServlet
{

    private static Logger logger = new Logger(RawCancel.class.getName());


    java.util.Date dt = null;
    ResultSet rs2 = null;

    public synchronized void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("Entering in RawCancel");
        String pathtoraw = request.getParameter("pathtoraw");
        String filename = request.getParameter("filename");
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        try
        {
            //FileInputStream rfis=new FileInputStream(pathtoraw + filename);
            //BufferedReader rin=new BufferedReader(new FileReader(pathtoraw + filename));

            File fin = new File(pathtoraw + filename);

            out.println("Raw File Deleted " + filename + " : " + fin.delete());

        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            logger.error("Raw File Not Found",ex);
            out.println(Functions.ShowMessage("Raw File Not Found", sw.toString()));

            //out.println("Raw File Not Found"+ex.toString());
            return;
        }


    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        service(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        service(request, response);
    }//post ends

    private String getBatchName()
    {
        return "" + System.currentTimeMillis() / 1000;
    }
}

