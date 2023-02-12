import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class PostPay extends HttpServlet
{

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        ServletContext session = getServletContext();
        PrintWriter pWriter = res.getWriter();

        session.setAttribute("PAN", req.getParameter("pan"));
        session.setAttribute("Expirydate", req.getParameter("expirydate"));
        session.setAttribute("PurchaseAmount", req.getParameter("purchaseAmount"));
        session.setAttribute("orderdesc", req.getParameter("orderdesc"));


        pWriter.println("<html>");
        pWriter.println("<body>");
        pWriter.println("<form name=\"postpay\" action=\"https://203.199.32.82/ocsmpi/merchant.asp\" method=\"post\" >");
        /*              pWriter.println("<input type=\"hidden\" name=\"PAN\" value=\"" + req.getParameter("PAN") + "\">");
                     pWriter.println("<input type=\"hidden\" name=\"expirydate\" value=\"" + req.getParameter("expirydate")+ "\">");

                     pWriter.println("<input type=\"hidden\" name=\"purchaseAmount\" value=\"" + req.getParameter("purchaseAmount") + "\">");
                     pWriter.println("<input type=\"hidden\" name=\"merchantID\" value=\"" + req.getParameter("merchantID") + "\">");
                     pWriter.println("<input type=\"hidden\" name=\"orderdesc\" value=\"" + req.getParameter("orderdesc") + "\">");
        */

        Enumeration enu = req.getParameterNames();
        while (enu.hasMoreElements())
        {
            String name = (String) enu.nextElement();
            pWriter.println("<input type=\"hidden\" name=\"" + name + "\" value=\"" + req.getParameter(name) + "\">");
        }

        pWriter.println("</form>");
        pWriter.println("<script language=\"javascript\">");
        pWriter.println("document.postpay.submit();");
        pWriter.println("</script>");
        pWriter.println("</body>");
        pWriter.println("</html>");

    }


}