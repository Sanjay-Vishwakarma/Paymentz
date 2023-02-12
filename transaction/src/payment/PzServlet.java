package payment;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by IntelliJ IDEA.
 * User: alpesh.s
 * Date: Nov 6, 2006
 * Time: 12:52:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class PzServlet extends HttpServlet
{
    private PzServletContext tsc = null;

    public PzServlet()
    {
        super();
    }

    public void init(ServletConfig servletConfig) throws ServletException
    {
        this.tsc =new PzServletContext(servletConfig.getServletContext());
        super.init(servletConfig);
    }

    public ServletContext getServletContext()
    {
        return this.tsc;
    }
}
