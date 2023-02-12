import com.directi.pg.Logger;
import com.directi.pg.core.mymonedero.SSLConnectionUtils;
import com.directi.pg.core.paymentgateway.MyMonederoPaymentGateway;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.directi.pg.core.valueObjects.MyMonederoRequestVO;
import com.directi.pg.core.valueObjects.MyMonederoResponseVO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Dhiresh
 * Date: 19/2/13
 * Time: 2:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyMonedero extends HttpServlet
{
    private static Logger log=new Logger(MyMonedero.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req, resp);
    }

    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        MyMonederoResponseVO ewalletResponseVO=new MyMonederoResponseVO();
        log.debug("Entering Main");
        MyMonederoPaymentGateway pg= new MyMonederoPaymentGateway();
        SSLConnectionUtils ssl=new SSLConnectionUtils();
        String args[]=new String[3];
        try{
        ssl.main(args);

        }
        catch(Exception e)
        {

            log.error("Exception occured in importing certificate",e);
        }

/*        try{
            log.debug("testing conn");
            pg.testPaymentStatus();
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
        }
*/


        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        genericTransDetailsVO.setAmount("100");
        genericTransDetailsVO.setCurrency("USD");
        genericTransDetailsVO.setOrderId("test order");
        genericTransDetailsVO.setOrderDesc("This is a test order");

        MyMonederoRequestVO ewalletRequestVO= new MyMonederoRequestVO(genericTransDetailsVO,"10103");

        try{
            ewalletResponseVO =(MyMonederoResponseVO) pg.processSale("100100100", ewalletRequestVO);
            log.debug("got response");

        }
        catch(Exception e)
        {
            log.error("Exception while the transaction has captured ",e);

        }
        log.debug("Error ---"+ewalletResponseVO.getError());
        log.debug("Status---"+ewalletResponseVO.getStatus());
        log.debug("Tracking ID--"+ewalletResponseVO.getTrackingid());
        log.debug("WCTXNID--"+ewalletResponseVO.getWctxnid());
        log.debug("redirecturl--"+ewalletResponseVO.getRedirecturl());



        RequestDispatcher rd= req.getRequestDispatcher("/myMonedero.jsp?req1=\""+ewalletResponseVO.getError()+"\"&req2=\""+ewalletResponseVO.getStatus()+"\"&req3=\""+ewalletResponseVO.getTrackingid()+"\"&req4=\""+ewalletResponseVO.getWctxnid()+"\"&req5=\""+ewalletResponseVO.getRedirecturl() +"\"" );
        rd.forward(req,resp);



    }

      
        

}
