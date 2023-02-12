import com.directi.pg.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class TestSettle extends HttpServlet
{

    static Logger logger = new Logger(UnblockIP.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in TestSettle");

          PrintWriter out = res.getWriter();


        try
        {

             ReconFileCreator.settleSBMTransactions(23,6,2012);
            ReconFileCreator.settleSBMTransactions(24,6,2012);
        }
        catch (SystemError se)
        {   logger.error("SystemError in TestSettle::::::",se);
            out.println(Functions.ShowMessage("Error", "Internal Error While Settle"));

            //System.out.println(se.toString());

        }
        catch (Exception e)
        {   logger.error("SystemError in TestSettle::::::",e);
           out.println(Functions.ShowMessage("Error", "Internal Error While Settle"));
        }

        out.println(Functions.ShowMessage("Sucess", "Done with Settlment"));
    }
}
