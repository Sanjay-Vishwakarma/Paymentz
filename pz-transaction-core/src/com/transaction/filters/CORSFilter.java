package com.transaction.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CORSFilter implements Filter {
    public void destroy() {
    }
    public static String VALID_METHODS = "DELETE, HEAD, GET, OPTIONS, POST, PUT";

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) resp;

        // No Origin header present means this is not a cross-domain request
        String origin = httpReq.getHeader("Origin");
        System.out.println("Origin------>"+origin);
        if (origin == null) {
            // Return standard response if OPTIONS request w/o Origin header
            System.out.println("httpReq.getMethod()---1--->"+httpReq.getMethod());
            if ("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
                httpResp.setHeader("Allow", VALID_METHODS);
                httpResp.setStatus(200);
                return;
            }
        }
        // Pass request down the chain, except for OPTIONS
        System.out.println("httpReq.getMethod()---2--->"+httpReq.getMethod());
        if (!"OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
            chain.doFilter(req, resp);
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
