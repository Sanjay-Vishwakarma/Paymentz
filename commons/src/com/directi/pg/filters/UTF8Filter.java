package com.directi.pg.filters;
import com.directi.pg.Logger;
import javax.servlet.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/30/14
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class UTF8Filter implements javax.servlet.Filter
{   static Logger logger = new Logger(UTF8Filter.class.getName());
    private String encoding;
    public void init(FilterConfig config) throws ServletException
    {
        logger.debug("Entering in UTF8Filter init");
        encoding = config.getInitParameter("requestEncoding");

        if(encoding==null) encoding="UTF-8";
    }
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)throws IOException,ServletException
    {
        logger.debug("Entering in UTF8Filter doFilter");
        if(null == req.getCharacterEncoding())
            req.setCharacterEncoding(encoding);

        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        chain.doFilter(req,resp);
    }
    public void destroy()
    {
        logger.debug("Entering in UTF8Filter destroy");
    }
}
