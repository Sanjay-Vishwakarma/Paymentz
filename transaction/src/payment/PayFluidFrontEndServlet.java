package payment;

import com.directi.pg.ActionEntry;
import com.directi.pg.AuditTrailVO;
import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

/**
 * Created by Admin on 12/21/2020.
 */
public class PayFluidFrontEndServlet  extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(PayFluidFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);

    }


    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionlogger.error("Entering PayFluidFrontEndServlet ......");


        HttpSession session = req.getSession();
        Functions functions = new Functions();
        //PayGateCryptoUtils payGateCryptoUtils = new PayGateCryptoUtils();
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        TransactionManager transactionManager = new TransactionManager();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        PaymentManager paymentManager = new PaymentManager();
        CommRequestVO requestVO = new CommRequestVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        StringBuilder responseMsg=new StringBuilder();

        BufferedReader br = req.getReader();
        Enumeration enumeration = req.getParameterNames();
        String str = "";
        String cardtype = "";
        BufferedReader br2=req.getReader();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);
            transactionlogger.error("---Key---PayFluidFrontEndServlet" + key + "---Value---" + value);
        }

        for (Object key : req.getParameterMap().keySet())
        {

            transactionlogger.error("----for loop PayFluidFrontEndServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

        String str2;
        while ((str2=br2.readLine())!=null)
        {
            responseMsg.append(str2);
        }
        transactionlogger.error("-----PayFluidFrontEndServlet JSON-----" + responseMsg);

    }
}
