/*
 * Copyright 1999 QSI Payments, Inc.
 * All rights reserved.  This precautionary copyright notice against
 * inadvertent publication is neither an acknowledgement of publication,
 * nor a waiver of confidentiality.
 *
 * Identification:
 *	$Id: ErrorPage.java,v 1.1 2012/10/21 22:48:26 cvs Exp $
 *
 * Description:
 *      Extracts, marks up and displays the error page.
 */

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author SimonM
 * @version $Revision: 1.1 $
 */
public class ErrorPage extends Object
{

    static public String CVS_ID = "@(#)$Id: ErrorPage.java,v 1.1 2012/10/21 22:48:26 cvs Exp $";

    private Exception m_exception = null;
    private HttpServletResponse m_servletResponse = null;

    /* ========================================================================
     *
     * Constructors
     */

    public ErrorPage(Exception exception,
                     HttpServletResponse httpServletResponse)
    {

        m_exception = exception;
        m_servletResponse = httpServletResponse;
    }

    /* ========================================================================
     *
     * Methods
     */

    /**
     * Extracts, marks up and displays the error page - if an exception is
     * provided (ie: not null), a stack trace is also output in order to
     * expedite the error correction process.
     *
     * @param title        the browser window title
     * @param errorHeading the main heading displayed on the error page
     * @param erroBodyText the body text of the error message - generally something like the
     *                     message obtained from an exception's getMessage method.
     */
    public void display(String title, String errorHeading, String errorBodyText)
            throws IOException
    {

        m_servletResponse.setContentType("text/html");
        PrintWriter out = m_servletResponse.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("<TITLE>" + title + "</TITLE>");
        out.println("</HEAD>");
        out.println("<BODY>");
        out.println("<H1>" + errorHeading + "</H1>");
        out.println("<P>");
        out.println("<B>" + errorBodyText + "</B>");

        // display a stack trace if m_exception is not null
        if (m_exception != null)
        {
            out.println("<P>");
            m_exception.printStackTrace(out);
        }
        out.println("</BODY>");
        out.println("</HTML>");
    }

}