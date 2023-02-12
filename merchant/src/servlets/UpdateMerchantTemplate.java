import com.directi.pg.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

//import java.io.PrintWriter;

//import java.util.*;



public class UpdateMerchantTemplate extends HttpServlet
{
    private static Logger Log = new Logger(UpdateMerchantTemplate.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        Log.debug("entering inside UpdateMerchantTemplate");
        response.setContentType("text/html");
       // PrintWriter out = response.getWriter();

        //Hashtable hash = null;
        String redirectpage = null;
        Merchants merchants = new Merchants();
        HttpSession session = request.getSession();
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!ESAPI.validator().isValidInput("EMAIL ",(String) request.getParameter("EMAILS"),"Email",100,false))
        {
            String message= "Please enter valid  Email Id";

            request.setAttribute("error",message);
            RequestDispatcher rd = request.getRequestDispatcher("/servlet/MerchantTemplate");
            rd.forward(request,response);
            return;
        }

        Hashtable detailhash = new Hashtable();
        Hashtable detailtemplatehash = new Hashtable();
        String name = "", value = "";
        try
        {

            boolean flag = true;
            Enumeration enumparanames = request.getParameterNames();
            while (enumparanames.hasMoreElements())
            {
                name = (String) enumparanames.nextElement();
                Log.debug("got name="+name);
                if (name.equalsIgnoreCase("List"))
                {
                    continue;
                }
                if (name.equalsIgnoreCase("save"))
                {
                    continue;
                }

                if (request.getParameter("template") == null)
                {
                    value = "N";
                }
                else
                {
                    value = "Y";
                }
                detailtemplatehash.put("template", value);

                if (request.getParameter("autoredirect") == null)
                {
                    value = "N";
                }
                else
                {
                    value = "Y";
                }

                detailtemplatehash.put("autoredirect", value);
                if (request.getParameter("checksumalgo") == null)
                {
                    value = "MD5";
                }
                else
                {
                    value = request.getParameter("checksumalgo");
                }
                detailtemplatehash.put("checksumalgo", value);

                //Log.debug("templatehash=" + detailtemplatehash);

                if (name.equals("HEADER"))
                {
                    String header = (String) request.getParameter("HEADER");

                   
                    header = Jsoup.clean(header, Whitelist.relaxed());

                    if (!Functions.isValidHeader(header) || !Functions.isValidSrt(header))
                    {
                        flag = false;
                    }
                    else
                    {

                        header = Functions.getFormat(header, "\\", "\\\\");
                        header = Functions.getFormat(header, "'", "''");
                        detailhash.put(name, header);
                    }
                }
                /*else if(name.equals("PHONE1")||name.equals("PHONE2"))
                {
                    if (!ESAPI.validator().isValidInput("PHONE ",(String) request.getParameter("PHONE1"),"SignupPhone",100,true)||!ESAPI.validator().isValidInput("PHONE ",(String) request.getParameter("PHONE2"),"SignupPhone",100,true))
                    {
                        flag = false;
                    }
                    else
                    {
                        detailhash.put("PHONE1", (String) request.getParameter("PHONE1"));
                        detailhash.put("PHONE1", (String) request.getParameter("PHONE2"));
                    }
                }
                else if (name.equals("EMAILS"))
                {

                    if (!ESAPI.validator().isValidInput("EMAIL ",(String) request.getParameter("EMAILS"),"Email",100,true))
                    {
                        flag = false;
                    }
                    else
                    {
                        detailhash.put("EMAILS", (String) request.getParameter("EMAILS"));
                    }
                }*/
                else
                {
                    if (!Functions.isValidSrt((String) request.getParameter(name)))
                    {
                        flag = false;
                    }
                    else
                    {
                        detailhash.put(name, (String) request.getParameter(name));
                    }
                }
                
            }

            if (flag)
            {
                Log.debug("Update the record");
                Template.ChangeTemplate(detailhash, (String) session.getAttribute("merchantid"));
                merchants.updateTemplate(detailtemplatehash, (String) session.getAttribute("merchantid"));
                Log.debug("redirecting to updatedtemplate.jsp");
                redirectpage = "/updatedtemplate.jsp?ctoken="+user.getCSRFToken();

            }
            else
            {
                redirectpage = "/merchanttemplate.jsp?MES=F&ctoken="+user.getCSRFToken();
            }

        }
        catch (SystemError se)
        {
            Log.error("Leaving Merchants throwing SQL Exception as System Error : " ,se);
            redirectpage = "/error.jsp";
        }
        catch (Exception e)
        {
            Log.error("Leaving Merchants throwing Exception : ",e);
            redirectpage = "/error.jsp";
        }
        request.setAttribute("details", detailhash);
        Log.debug("Leaving UpdateMerchantTemplate  ");
        Log.debug("redirectpage  " + redirectpage);
        RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
        rd.forward(request, response);


    }
}
