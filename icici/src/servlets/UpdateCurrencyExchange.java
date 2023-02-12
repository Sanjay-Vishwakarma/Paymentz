import com.directi.pg.*;
import com.manager.CurrencyExchangeManager;
import com.manager.vo.ExchangeRatesVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: Jitendra
 * Date: 03/03/2018
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateCurrencyExchange extends HttpServlet
{
    private static Logger logger=new Logger(UpdateCurrencyExchange.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session=Functions.getNewSession(request);
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if(!Admin.isLoggedIn(session)){
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        String EOL="<BR>";

        String action=request.getParameter("action");
        String mappingId=request.getParameter("id");

        StringBuffer msg = new StringBuffer();
        RequestDispatcher rd=null;

        logger.debug("action:"+action);
        logger.debug("mappingId:"+mappingId);

        CurrencyExchangeManager currencyExchangeManager=new CurrencyExchangeManager();

        if("modify".equals(action))
        {
            try
            {
                ExchangeRatesVO exchangeRatesVO=currencyExchangeManager.getExchangeDetails(mappingId);
                if(exchangeRatesVO!=null){
                    rd=request.getRequestDispatcher("/singleExchangeRateDetails.jsp?ctoken="+user.getCSRFToken());
                    request.setAttribute("exchangeRatesVO",exchangeRatesVO);
                    rd.forward(request, response);
                    return;
                }
                else{
                    logger.error("No record found");
                    rd=request.getRequestDispatcher("/singleExchangeRateDetails.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
            }
            catch (PZDBViolationException e)
            {
                logger.error("PZDBViolationException:::::" + e);
                msg.append("Internal error while processing your request");
                request.setAttribute("msg",msg.toString());
                rd=request.getRequestDispatcher("/singleExchangeRateDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            catch (ParseException e)
            {
                logger.error("ParseException:::::" + e);
                msg.append("Internal error while processing your request");
                request.setAttribute("msg",msg.toString());
                rd=request.getRequestDispatcher("/singleExchangeRateDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }
        else if("update".equals(action))
        {
            try
            {
                String exchangeRate= request.getParameter("exchangerate");
                if(!ESAPI.validator().isValidInput("exchangerate", exchangeRate, "Amount5Digit", 20, false)){
                    msg.append("Invalid exchange rate");
                }
                if(!(msg.length() >0))
                {
                    if(Double.valueOf(exchangeRate)==0 )
                    {
                        msg.append("Invalid exchange rate");

                    }
                }


                if(msg.length()>0){
                    ExchangeRatesVO exchangeRatesVO= null;
                    try
                    {
                        exchangeRatesVO = currencyExchangeManager.getExchangeDetails(mappingId);
                        request.setAttribute("exchangeRatesVO",exchangeRatesVO);
                        request.setAttribute("msg",msg.toString());
                        //System.out.println("msg.toString():::::"+msg.toString());
                        rd=request.getRequestDispatcher("/singleExchangeRateDetails.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }
                    catch(ParseException e)
                    {
                        logger.error("ParseException:::::"+e);
                        msg.append("Internal error while processing your request");
                        rd=request.getRequestDispatcher("/singleExchangeRateDetails.jsp?ctoken="+user.getCSRFToken());
                        request.setAttribute("msg",msg.toString());
                        rd.forward(request, response);
                        return;
                    }
                }
                boolean result=currencyExchangeManager.updateExchangeDetails(mappingId,exchangeRate);
                if(result){
                    rd=request.getRequestDispatcher("/listExchangeRates.jsp?ctoken="+user.getCSRFToken());
                    msg.append("Updated Successfully");
                    request.setAttribute("msg",msg.toString());
                    rd.forward(request, response);
                    return;
                }
                else{
                    rd=request.getRequestDispatcher("/listExchangeRates.jsp?ctoken="+user.getCSRFToken());
                    msg.append("update failed");
                    request.setAttribute("msg", msg.toString());
                    rd.forward(request,response);
                    return;
                }
            }
            catch (PZDBViolationException e)
            {
                logger.error("PZDBViolationException:::::" + e);
                msg.append("Internal error while processing your request");
                request.setAttribute("msg", msg.toString());
                rd = request.getRequestDispatcher("/singleExchangeRateDetails.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }
        else
        {
            logger.error("Invalid action");
            rd=request.getRequestDispatcher("/listExchangeRates.jsp?ctoken="+user.getCSRFToken());
            msg.append("Invalid action");
            request.setAttribute("msg",msg.toString());
            rd.forward(request,response);
            return;
        }
    }
}
