import com.directi.pg.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;


public class FileRemoveServlet extends HttpServlet
{
    private static Logger Log = new Logger(FileRemoveServlet.class.getName());

    public FileRemoveServlet()
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

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        Log.info("Inside FileRemoveServlet");
        Merchants merchants = new Merchants();
        res.setContentType("text/html");
        HttpSession session = req.getSession();
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/logout.jsp");
            return;
        }


        StringBuffer strbuf = new StringBuffer();
        Enumeration enumparanames = req.getParameterNames();
        while (enumparanames.hasMoreElements())
        {
            String paraname = (String) enumparanames.nextElement();
            strbuf.append(paraname + "=" + req.getParameter(paraname) + "&");
        }

        try
        {

            String memberid = (String) session.getAttribute("merchantid");

            ServletContext merctx = getServletContext();
            ServletContext tempctx = merctx.getContext("/newtemplates");
            String dbPath = "/images";
            String uploadPath = tempctx.getRealPath(dbPath);

            Log.debug("uploadPath="+uploadPath);

            String filename = "", name = "";

            filename = req.getParameter("filename");
            name = req.getParameter("rname");
            FileUploadBean fub = new FileUploadBean();
            fub.setSavePath(uploadPath);
            fub.setNewFilename(filename);
            boolean flag = false;
            try
            {
                flag = fub.doRemove();
            }
            catch (SystemError error)
            {
                Log.error("file not found.", error);
                RequestDispatcher rd = req.getRequestDispatcher("/addImages.jsp?remove=notfound&" + strbuf.toString());
                rd.forward(req, res);
                return;
            }
            catch (Exception e)
            {
                Log.error("exception while removing image ",e);
                RequestDispatcher rd = req.getRequestDispatcher("/addImages.jsp?remove=e&" + strbuf.toString());
                rd.forward(req, res);
                return;
            }
            if (flag)
            {

                Template.removeFile(name, memberid);
                Log.debug("successfully remove ");
                RequestDispatcher rd = req.getRequestDispatcher("/addImages.jsp?" + strbuf.toString());
                rd.forward(req, res);
                return;
            }


        }
        catch (Exception e)
        {
            Log.error("exceprion while uploading image ",e);
            RequestDispatcher rd = req.getRequestDispatcher("/addImages.jsp?remove=e&" + strbuf.toString());
            rd.forward(req, res);
            return;
        }

    }


}
