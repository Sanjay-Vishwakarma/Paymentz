import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudRuleChangeTrackerVO;
import com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.exceptionHandler.PZDBViolationException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Sneha on 24/8/15.
 */
public class FraudRuleSendIntimation extends HttpServlet
{
    private static Logger logger = new Logger(FraudRuleSendIntimation.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        doProcess(request, response);
    }
    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("Entering into FraudRuleSendIntimation");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        RequestDispatcher rd = request.getRequestDispatcher("/fraudRuleChangeIntimationList.jsp?ctoken=" + user.getCSRFToken());
        String[] intimationids = request.getParameterValues("intimationid");
        String toid = request.getParameter("fsaccountid");
        FraudRuleManager ruleManager = new FraudRuleManager();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Rule Intimation");
        HSSFRow header = sheet.createRow(0);

        StringBuffer msg = new StringBuffer();
        int srno = 1;
        String isStatusIntimated = "false";
        String newscore = null;
        String newstatus = null;
        String rulename = null;
        String filepath = null;
        String subaccountname = null;
        String filename = null;
        String previousScore = null;
        String previousStatus = null;

        try
        {
            if (intimationids != null)
            {
                sheet.setColumnWidth((short) 3, (short) 202000);
                sheet.setColumnWidth((short) 4, (short) 205000);
                sheet.setColumnWidth((short) 5, (short) 200000);
                sheet.setColumnWidth((short) 6, (short) 200000);
                sheet.setColumnWidth((short) 7, (short) 200000);
                sheet.setColumnWidth((short) 8, (short) 200000);

                HSSFCell cell0 = header.createCell((short) 0);
                HSSFCell cell1 = header.createCell((short) 1);
                HSSFCell cell2 = header.createCell((short) 2);
                HSSFCell cell3 = header.createCell((short) 3);
                HSSFCell cell4 = header.createCell((short) 4);
                HSSFCell cell5 = header.createCell((short) 5);
                HSSFCell cell6 = header.createCell((short) 6);
                HSSFCell cell7 = header.createCell((short) 7);
                HSSFCell cell8 = header.createCell((short) 8);

                HSSFCellStyle style = workbook.createCellStyle();
                style.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index);
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                style.setBorderLeft((short) 1);
                style.setBorderRight((short) 1);
                style.setBorderTop((short) 1);
                style.setBorderBottom((short) 1);

                cell2.setCellStyle(style);
                cell3.setCellStyle(style);
                cell4.setCellStyle(style);
                cell5.setCellStyle(style);
                cell6.setCellStyle(style);
                cell7.setCellStyle(style);
                cell8.setCellStyle(style);

                cell2.setCellValue("Sr No.");
                cell3.setCellValue("Website");
                cell4.setCellValue("Rule Name");
                cell5.setCellValue("New Score");
                cell6.setCellValue("Existing Score");
                cell7.setCellValue("New Status");
                cell8.setCellValue("Existing Status");

                isStatusIntimated = ruleManager.isStatusIntimated(intimationids);
                if("false".equalsIgnoreCase(isStatusIntimated))
                {
                    List<FraudRuleChangeTrackerVO> changeTrackerVOsList = ruleManager.getFraudRuleChangeTracker(intimationids);
                    for (FraudRuleChangeTrackerVO changeTrackerVO : changeTrackerVOsList)
                    {
                        newscore = changeTrackerVO.getNewScore();
                        newstatus = changeTrackerVO.getNewStatus();
                        rulename = changeTrackerVO.getRuleName();
                        subaccountname = changeTrackerVO.getFsSubAccountName();
                        previousScore = changeTrackerVO.getPreviousScore();
                        previousStatus = changeTrackerVO.getPreviousStatus();

                        HSSFRow row = sheet.createRow((short) srno);

                        HSSFCell cellr0 = row.createCell((short) 2);
                        HSSFCell cellr1 = row.createCell((short) 3);
                        HSSFCell cellr2 = row.createCell((short) 4);
                        HSSFCell cellr3 = row.createCell((short) 5);
                        HSSFCell cellr4 = row.createCell((short) 6);
                        HSSFCell cellr5 = row.createCell((short) 7);
                        HSSFCell cellr6 = row.createCell((short) 8);

                        cellr0.setCellStyle(style);
                        cellr1.setCellStyle(style);
                        cellr2.setCellStyle(style);
                        cellr3.setCellStyle(style);
                        cellr4.setCellStyle(style);
                        cellr5.setCellStyle(style);
                        cellr6.setCellStyle(style);

                        cellr0.setCellValue(srno);
                        cellr1.setCellValue(subaccountname);
                        cellr2.setCellValue(rulename);
                        cellr3.setCellValue(newscore);
                        cellr4.setCellValue(previousScore);
                        cellr5.setCellValue(newstatus);
                        cellr6.setCellValue(previousStatus);
                        srno++;
                    }
                }
                else
                {
                    msg.append("Invalid Intimation.");
                    request.setAttribute("msg", msg.toString());
                    rd.forward(request, response);
                    return;
                }

                FraudSystemAccountVO accountVO = ruleManager.getFraudAccountDetails(toid);
                String accountId = accountVO.getAccountName();
                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentSystemDate = dateFormater.format(new Date());
                filename = "RuleChangeIntimation_" + accountId + "_" + currentSystemDate;
                filename = filename + ".xls";

                filepath = ApplicationProperties.getProperty("FRAUD_RULE_CHANGE");
                filepath = filepath + filename;

                FileOutputStream fileOut = new FileOutputStream(filepath);
                workbook.write(fileOut);
                fileOut.close();

                HashMap<MailPlaceHolder,String> mailContant=new HashMap<MailPlaceHolder,String>();
                mailContant.put(MailPlaceHolder.TOID,toid);
                mailContant.put(MailPlaceHolder.FRAUDACCOUNTID,accountId);
                mailContant.put(MailPlaceHolder.ATTACHMENTFILENAME,filename);
                mailContant.put(MailPlaceHolder.ATTACHMENTFILEPATH,filepath);

                //MailService mailService = new MailService();
                AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                asynchronousMailService.sendMerchantSignup(MailEventEnum.FRAUDRULE_CHANGE_INTIMATION, mailContant);

                for(String intimationId : intimationids)
                {
                    ruleManager.updateRuleIntimationStatus(intimationId);
                }
                msg.append("MAIL SENT");
            }

            request.setAttribute("msg", msg.toString());
            rd.forward(request, response);
        }
        catch (PZDBViolationException e)
        {
           logger.error("Catch PZDBViolationException...",e);
        }
    }
}
