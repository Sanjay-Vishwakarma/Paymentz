import com.directi.pg.*;
import com.manager.TokenManager;
import com.manager.vo.PaginationVO;
import com.manager.vo.TokenDetailsVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ListOfMerchantRegistrations extends HttpServlet
{
    private static Logger log = new Logger(ListOfMerchantRegistrations.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        List<TokenDetailsVO> tokenDetailsVOList = null;
        HttpSession session = req.getSession();
        log.debug("Enter in ListOfMerchantRegistrations");
        Merchants merchants = new Merchants();
        Functions functions= new Functions();

        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String merchantid = (String) session.getAttribute("merchantid");
        session.setAttribute("submit","Registration History");

        String fDate= null;
        String tDate = null;
        String firstName=null;
        String lastName=null;
        String email=null;
        String description=null;

        description = req.getParameter("description");
        firstName=req.getParameter("firstname");
        lastName=req.getParameter("lastname");
        email=req.getParameter("email");

        StringBuffer  sb=new StringBuffer();
        RequestDispatcher rd = req.getRequestDispatcher("/listOfMerchantRegistrations.jsp?ctoken="+user.getCSRFToken());

        if(!ESAPI.validator().isValidInput("firstname", firstName, "contactName", 50, true))
        {
            sb.append("Invalid First Name, \n");
        }
        if(!ESAPI.validator().isValidInput("lastname", lastName, "contactName", 50, true))
        {
            sb.append("Invalid Last Name, \n");
        }
        if(!ESAPI.validator().isValidInput("cardholderemail", email, "Email", 50, true))
        {
            sb.append("Invalid Email, \n");
        }
        if(sb.length()>0)
        {
            req.setAttribute("error",(sb.substring(0, sb.length()-3)));
            rd.forward(req,res);
            return;
        }
        res.setContentType("text/html");
        PaginationVO paginationVO = new PaginationVO();

        String fdate = "";
        String fmonth = "";
        String fyear = "";
        String tdate = "";
        String tmonth = "";
        String tyear = "";

        if (functions.isValueNull(req.getParameter("fdate")))
        {
            String[] startDate = req.getParameter("fdate").split("/");
            fdate = startDate[0];
            fmonth = startDate[1];
            fyear = startDate[2];
        }

        if (functions.isValueNull(req.getParameter("tdate")))
        {
            String[] endDate = (req.getParameter("tdate")).split("/");
            tdate = endDate[0];
            tmonth = endDate[1];
            tyear = endDate[2];
        }

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        try
        {
            TokenManager tokenManager=new TokenManager();

            SimpleDateFormat actualFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat actualFormat1 = new SimpleDateFormat("yyyy-MM-dd");

            fDate=req.getParameter("fdate");
            tDate=req.getParameter("tdate");

            if(functions.isFutureDateComparisonWithFromAndToDate(fDate, tDate, "dd/MM/yyyy"))
            {
                req.setAttribute("error","Invalid From & To date");
                rd.forward(req, res);
                return;
            }

            paginationVO.setInputs("memberid=" + merchantid + "&fdate=" + fDate + "&tdate=" + tDate + "&firstname="+ firstName +"&lastname=" + lastName +"&description=" + description + "&email=" +email  );
            paginationVO.setPage("ListOfMerchantRegistrations");
            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));

            fDate=actualFormat1.format(actualFormat.parse(fDate))+ " 00:00:00";
            tDate=actualFormat1.format(actualFormat.parse(tDate))+ " 23:59:59";

            tokenDetailsVOList = tokenManager.getMerchantRegistrationList(merchantid,fdtstamp,tdtstamp,description,firstName,lastName,email,paginationVO);

            req.setAttribute("fdate", req.getParameter("fdate"));
            req.setAttribute("tdate", req.getParameter("tdate"));
            req.setAttribute("paginationVO", paginationVO);
            req.setAttribute("listOfMerchantRegistrations", tokenDetailsVOList);
            rd.forward(req, res);
        }
        catch (Exception dbe)
        {
            log.error("Exception in ListOfMerchantRegistrations---", dbe);
            req.setAttribute("error","Internal error while processing your request, please contact support.");
            rd.forward(req, res);
        }
    }
}
