import com.directi.pg.Admin;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZSettlementFile;
import com.payment.response.PZSettlementRecord;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/26/13
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonSettlementFile extends HttpServlet
{
    private static Logger log = new Logger(CommonSettlementFile.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        log.debug("ctoken==="+req.getParameter("ctoken"));


        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String pathtoraw = null;
        pathtoraw = ApplicationProperties.getProperty("MPR_FILE_STORE");
        FileUploadBean fub = new FileUploadBean();
        fub.setSavePath(pathtoraw);
        try
        {
            fub.doUpload(req,null);
        }
        catch(SystemError sys)
        {
            //error msg
            log.error("File is exist",sys);
        }
        PZSettlementFile Request = new PZSettlementFile();

        List<PZSettlementRecord> vTransactions = new ArrayList<PZSettlementRecord>();
        String File=fub.getFilename();
        String str="";
        Connection con = null;
        TransactionEntry transactionEntry = null;
        String accountid=fub.getFieldValue("accountid");
        String tableName="";

        String filepath=ApplicationProperties.getProperty("MPR_FILE_STORE")+File;
        AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(null, Integer.parseInt(accountid));
        Request.setFilepath(filepath);
        Request.setAccountId(Integer.parseInt(accountid));
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountid);
            GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
            tableName = Database.getTableNameForSettlement(gatewayType.getGateway());

            vTransactions = process.readSettlementFile(Request);
        }
        catch (SystemError systemError)
        {
            log.error("SYSTEM ERROR",systemError);
        }
        String val=null;
        if(vTransactions!=null)
        {
            AuditTrailVO auditTrailVO = new AuditTrailVO();
            auditTrailVO.setActionExecutorId(String.valueOf("1"));
            auditTrailVO.setActionExecutorName("Admin");

            CommonPaymentProcess proc=new CommonPaymentProcess();
            val = proc.processSettlement(Integer.parseInt(accountid), vTransactions, process.getAdminEmailAddress(), auditTrailVO,tableName);
        }
        if(val!=null)
        {
            req.setAttribute("res",val);
            RequestDispatcher rd = req.getRequestDispatcher("/commonsettlecron.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);

        }
    }
}
