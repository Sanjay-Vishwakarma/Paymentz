package payment;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.common.core.CommResponseVO;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Admin on 1/19/2022.
 */
public class OPBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger=new TransactionLogger(OPBackEndServlet.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        transactionLogger.error("----Inside OPBackEndServlet----");
        Functions functions = new Functions();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        StringBuilder responseMsg = new StringBuilder();
        TransactionManager transactionManager = new TransactionManager();
        MerchantDAO merchantDAO = new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        ActionEntry entry = new ActionEntry();
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();

        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }
        transactionLogger.error("-----OPBackEndServlet Notification JSON-----" + responseMsg);
        response.setStatus(200);
        return;
    }

}
