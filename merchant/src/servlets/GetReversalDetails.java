import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.TransactionDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Ecospend.EcospendPaymentGateway;
import com.payment.Ecospend.EcospendRequestVo;
import com.payment.Ecospend.EcospendResponseVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.validators.vo.CommonValidatorVO;
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
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

public class GetReversalDetails extends HttpServlet
{

    private static Logger logger = new Logger(GetReversalDetails.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        PrintWriter printwriter = res.getWriter();
        Merchants merchants = new Merchants();
        Functions functions=new Functions();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        logger.debug("CSRF check successful ");
     //   String merchantid = (String) session.getAttribute("merchantid");


        //int start = 0; // start index
       // int end = 0; // end index
      //  String str = null;


        res.setContentType("text/html");

        //PrintWriter out = res.getWriter();

       // String data = req.getParameter("data");

        //String icicitransid = Functions.checkStringNull(req.getParameter("icicitransid"));
        String icicitransid = null;
        String accountid=null;
        String toid="";
        String marketPlaceFlag="";
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
        Date d1=new Date();
        //Functions fn=new Functions();
        Hashtable hash = null;
        Hashtable parenthash=null;
        Hashtable childhash=null;
        logger.info("Get particuler reversal detail from  trckingid");
        String gateway =null;
        if(functions.isValueNull(accountid))
        {
            gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
        }
        String tableName = Database.getTableName(gateway);
        StringBuilder query =null;
        if(tableName.equals("transaction_icicicredit"))
        {
        query = new StringBuilder("select toid,status,icicitransid,transid,description,captureamount,refundamount,accountid from transaction_icicicredit where icicitransid= ? ");
        }
        else if(tableName.equals("transaction_common"))
        {
            query = new StringBuilder("select toid,status,trackingid as icicitransid,transid,description,currency,captureamount,refundamount,accountid,parentTrackingid from ").append(tableName).append(" where trackingid= ? ");
        }
        else
        {
        query = new StringBuilder("select toid,status,trackingid as icicitransid,transid,description,currency,captureamount,refundamount,accountid from ").append(tableName).append(" where trackingid= ? ");
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getConnection();
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,icicitransid);
            hash = Database.getHashFromResultSet(pstmt.executeQuery());
            if(hash!=null)
            {
                Hashtable temphash=(Hashtable)hash.get("1");
                toid=(String) temphash.get("toid");
                if(functions.isValueNull((String) temphash.get("parentTrackingid")))
                {
                    query = new StringBuilder("select toid,status,trackingid as icicitransid,transid,description,currency,captureamount,refundamount,accountid,parentTrackingid from ").append(tableName).append(" where trackingid= ? ");
                    pstmt = conn.prepareStatement(query.toString());
                    pstmt.setString(1,(String) temphash.get("parentTrackingid"));
                    logger.error("pstmt1----"+pstmt);
                    parenthash = Database.getHashFromResultSet(pstmt.executeQuery());
                    if(parenthash!=null)
                        temphash=(Hashtable)parenthash.get("1");
                    toid=(String)temphash.get("toid");
                }
                else
                {
                    query = new StringBuilder("select status,trackingid as icicitransid,transid,description,currency,captureamount,refundamount,accountid,parentTrackingid from ").append(tableName).append(" where parentTrackingid= ? and captureamount > refundamount ");
                    pstmt = conn.prepareStatement(query.toString());
                    pstmt.setString(1,(String) temphash.get("icicitransid"));
                    childhash = Database.getHashFromResultSet(pstmt.executeQuery());
                }
                merchantDetailsVO=merchantDAO.getMemberDetails(toid);
            }

            TransactionDAO transactionDAO = new TransactionDAO();
            TransactionDetailsVO transactionDetailsVO = transactionDAO.getDetailFromCommon(icicitransid);

            if("ecospend".equals(transactionDetailsVO.getFromtype()))
            {

                EcospendPaymentGateway ecospendPaymentGateway = new EcospendPaymentGateway(transactionDetailsVO.getAccountId());
                EcospendRequestVo ecospendRequestVo = new EcospendRequestVo();
                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();

                commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
                commTransactionDetailsVO.setCardType(transactionDetailsVO.getCardTypeId());
                ecospendRequestVo.setAddressDetailsVO(commAddressDetailsVO);
                ecospendRequestVo.setTransDetailsVO(commTransactionDetailsVO);
                EcospendResponseVO ecospendResponseVO = (EcospendResponseVO) ecospendPaymentGateway.processInitialSale(icicitransid, ecospendRequestVo);
                TreeMap<String, String> bankMap = ecospendResponseVO.getBankid();

                TreeMap<String, String> bankDetailsMap = new TreeMap<>();

                for (Map.Entry<String, String> entry : bankMap.entrySet())
                {
                    bankDetailsMap.put(entry.getKey(), entry.getValue().split("_")[0]);
                }

                logger.error("bankDetailsMap ====== " + bankDetailsMap);

                req.setAttribute("bankDetailsMap", bankDetailsMap);
            }

            req.setAttribute("reversaldetails", hash);
            req.setAttribute("parentreversaldetails",parenthash);
            req.setAttribute("childreversaldetails",childhash);
            req.setAttribute("merchantDetailsVO",merchantDetailsVO);
            req.setAttribute("transactionDetailsVO",transactionDetailsVO);
            RequestDispatcher rd = req.getRequestDispatcher("/reversereason.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {
            logger.error("System Error occur while fetch record of reversal",se);
            printwriter.println(Functions.NewShowConfirmation1("Error!", "Internal System Error While getting Reversal Detail"));
            //System.out.println(se.toString());

        }
        catch (Exception e)
        {
            logger.error("Exception occur while fetch record of reversal",e);
            printwriter.println(Functions.NewShowConfirmation1("Error!", "Internal System Error While getting Reversal Detail"));
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

    }
}
