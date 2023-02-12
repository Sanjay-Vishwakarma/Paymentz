import com.directi.pg.Admin;
import com.directi.pg.Logger;
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

public class addNewExchangeRate extends HttpServlet
{
    static Logger logger = new Logger(addNewExchangeRate.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("Entering Into addNewExchangeRate");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }
        RequestDispatcher rd = request.getRequestDispatcher("/addNewExchangeRate.jsp?ctoken="+user.getCSRFToken());

        StringBuffer msg = new StringBuffer();
        String EOL="<BR>";

        String fromCurrency=request.getParameter("fromcurrency");
        String toCurrency=request.getParameter("tocurrency");
        String exchangeRate=request.getParameter("exchangerate");

        if(!ESAPI.validator().isValidInput("fromcurrency", fromCurrency, "StrictString", 3, false)){
            msg.append("Invalid from currency"+EOL);
        }
        if(!ESAPI.validator().isValidInput("tocurrency", toCurrency, "StrictString", 3, false)){
            msg.append("Invalid to currency"+EOL);
        }
        if(!ESAPI.validator().isValidInput("exchangerate", exchangeRate, "Amount5Digit", 20, false)){
            msg.append("Invalid exchange rate"+EOL);
        }
        if(!(msg.length() >0))
        {
            if(Double.valueOf(exchangeRate)==0 )
            {
                msg.append("Invalid exchange rate");

            }
        }


        if(msg.length()>0){
            request.setAttribute("msg",msg.toString());
            rd.forward(request, response);
            return;
        }
        if (fromCurrency.equalsIgnoreCase(toCurrency))
        {
            msg.append("Mapping Not Valid");
            request.setAttribute("msg",msg.toString());
            rd.forward(request, response);
            return;
        }

        ExchangeRatesVO exchangeRatesVO=new ExchangeRatesVO();
        exchangeRatesVO.setFromCurrency(fromCurrency);
        exchangeRatesVO.setToCurrency(toCurrency);
        exchangeRatesVO.setExchangeValue(Double.valueOf(exchangeRate));

        CurrencyExchangeManager currencyExchangeManager=new CurrencyExchangeManager();
        try{
            boolean availabilityCheck=currencyExchangeManager.checkAvailability(exchangeRatesVO);
            if(!availabilityCheck){
                boolean result=currencyExchangeManager.addExchangeRate(exchangeRatesVO);
                if(result){
                    msg.append("Mapping Added Successfully");
                }
                else{
                    msg.append("Mapping failed");
                }
            }
            else{
                msg.append("Mapping already available");
            }
            request.setAttribute("msg",msg.toString());
            rd.forward(request, response);
            return;
        }
        catch(PZDBViolationException e){
            logger.error("PZDBViolationException:::::" + e);
            request.setAttribute("msg",msg.toString());
            rd.forward(request, response);
            return;
        }
    }
}
