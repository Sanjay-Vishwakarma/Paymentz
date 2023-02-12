package net.partner;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Hashtable;

public class GetRefundDetails extends HttpServlet
{

    private static Logger logger = new Logger(GetRefundDetails.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        PrintWriter printwriter = res.getWriter();
        PartnerFunctions partner=new PartnerFunctions();
        Functions functions = new Functions();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        logger.debug("CSRF check successful ");
        //String partnerid = (String) session.getAttribute("merchantid");


        /*int start = 0; // start index
        int end = 0; // end index
        String str = null;*/


        res.setContentType("text/html");

        //PrintWriter out = res.getWriter();

        //String data = req.getParameter("data");

        String memberid = req.getParameter("memberid");
        req.setAttribute("memberid",memberid);

        String description = req.getParameter("description");
        req.setAttribute("description",description);

        //String icicitransid = Functions.checkStringNull(req.getParameter("icicitransid"));
        String icicitransid = null;
        String accountid=null;
        try
        {
            icicitransid = ESAPI.validator().getValidInput("icicitransid",req.getParameter("icicitransid"),"Numbers",25,false);
            accountid=  ESAPI.validator().getValidInput("accountid",req.getParameter("accountid"),"Numbers",25,false);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid input",e);
            icicitransid= null;
        }

        //Functions fn=new Functions();
        Hashtable hash = null;
        Hashtable parenthash=null;
        Hashtable childhash=null;
        logger.info("Get particuler reversal detail from  trackingid");
        String gateway =null;
        if(functions.isValueNull(accountid))
        {
            gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
        }
        String tableName = Database.getTableName(gateway);
        StringBuffer query =null;
        if(tableName.equals("transaction_icicicredit"))
        {
            query = new StringBuffer("select toid,status,icicitransid,transid,description,captureamount,refundamount,accountid from transaction_icicicredit where icicitransid= ? ");
        }
        else if(tableName.equals("transaction_common"))
        {
            query = new StringBuffer("select toid,status,trackingid as icicitransid,transid,description,currency,captureamount,refundamount,accountid,parentTrackingid from transaction_common where trackingid= ? ");
        }
        else
        {
            query = new StringBuffer("select toid,status,trackingid as icicitransid,transid,description,currency,captureamount,refundamount,accountid from " + tableName + " where trackingid= ? ");
        }
        logger.debug("query=="+query.toString());

        String toid="";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getConnection();
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, icicitransid);
            hash = Database.getHashFromResultSet(pstmt.executeQuery());
            if(hash!=null)
            {
                Hashtable temphash=(Hashtable)hash.get("1");
                toid=(String)temphash.get("toid");
                if(functions.isValueNull((String) temphash.get("parentTrackingid")))
                {
                    query = new StringBuffer("select toid,status,trackingid as icicitransid,transid,description,currency,captureamount,refundamount,accountid,parentTrackingid from ").append(tableName).append(" where trackingid= ? ");
                    pstmt = conn.prepareStatement(query.toString());
                    pstmt.setString(1,(String) temphash.get("parentTrackingid"));
                    logger.error("pstmt----"+pstmt);
                    parenthash = Database.getHashFromResultSet(pstmt.executeQuery());
                    if(parenthash!=null)
                        temphash=(Hashtable)parenthash.get("1");
                    toid=(String)temphash.get("toid");
                }
                else
                {
                    query = new StringBuffer("select status,trackingid as icicitransid,transid,description,currency,captureamount,refundamount,accountid,parentTrackingid from ").append(tableName).append(" where parentTrackingid= ? and captureamount > refundamount");
                    pstmt = conn.prepareStatement(query.toString());
                    pstmt.setString(1,(String) temphash.get("icicitransid"));
                    logger.error("pstmt----"+pstmt);
                    childhash = Database.getHashFromResultSet(pstmt.executeQuery());
                }
                merchantDetailsVO=merchantDAO.getMemberDetails(toid);
            }
            req.setAttribute("parentreversaldetails",parenthash);
            req.setAttribute("childreversaldetails",childhash);
            req.setAttribute("refunddetails", hash);
            req.setAttribute("merchantDetailsVO",merchantDetailsVO);

            RequestDispatcher rd = req.getRequestDispatcher("/reversereason.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {
            logger.error("System Error occur while fetching record of refund",se);
            printwriter.println(Functions.NewShowConfirmation1("Error!", "Internal System Error While getting Refund Detail"));
            //System.out.println(se.toString());

        }
        catch (Exception e)
        {
            logger.error("Exception occure while fetching record of refund",e);
            printwriter.println(Functions.NewShowConfirmation1("Error!", "Internal System Error While getting Refund Detail"));
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

    }
}
