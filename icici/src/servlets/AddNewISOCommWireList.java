import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.ISOCommissionGenerator;
import com.manager.TransactionManager;
import com.manager.dao.CommissionManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.CommissionVO;
import com.manager.vo.ISOCommReportVO;
import com.manager.vo.merchantmonitoring.DateVO;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
/**
 * Created by admin on 8/10/2016.
 */
public class AddNewISOCommWireList extends HttpServlet
{
    public static Logger log=new Logger(AddNewISOCommWireList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rd = request.getRequestDispatcher("/addnewisocommwire.jsp?&ctoken=" + user.getCSRFToken());
        String accountId = request.getParameter("accountid");
        String dynamicCommissionIds=request.getParameter("dynamiccommissionids");

        //validation check
        StringBuilder sbError = new StringBuilder();
        if (!ESAPI.validator().isValidInput("dynamiccommissionids", request.getParameter("dynamiccommissionids"), "CommaSeprateNum", 100, true))
        {
            sbError.append("Invalid Dynamic Commission Id<BR>");
        }
        if (!ESAPI.validator().isValidInput("accountid", request.getParameter("accountid"), "Numbers", 10, false))
        {
            sbError.append("Invalid Account Id<BR>");
        }
        if (!ESAPI.validator().isValidInput("firstdate", request.getParameter("firstdate"), "fromDate", 16, false))
        {
            sbError.append("Invalid Start Date<BR>");
        }
        if (!ESAPI.validator().isValidInput("settledstarttime", request.getParameter("settledstarttime"), "time", 255, false))
        {
            sbError.append("Invalid Start Time<BR>");
        }
        if (!ESAPI.validator().isValidInput("lastdate", request.getParameter("lastdate"), "fromDate", 16, false))
        {
            sbError.append("Invalid End Date<BR>");
        }
        if (!ESAPI.validator().isValidInput("settledendtime", request.getParameter("settledendtime"), "time", 255, false))
        {
            sbError.append("Invalid End Time<BR>");
        }

        HashMap<String,String> dynamicInputValues=new HashMap();
        CommissionManager commissionManager=new CommissionManager();
        Functions functions=new Functions();
        if(functions.isValueNull(dynamicCommissionIds))
        {
            //System.out.println("dynamicCommissionIds====" + dynamicCommissionIds);
            String dynamicIds[]=dynamicCommissionIds.split(",");
            for (String id:dynamicIds)
            {
                String dCommName=request.getParameter("dynamicchargename_"+id);
                String dCommValueType=request.getParameter("dynamicchargetype_"+id);
                String dCommValue=request.getParameter("dynamicchargevalue_"+id);
                if("Percentage".equals(dCommValueType))
                {
                    if (!ESAPI.validator().isValidInput(dCommName,dCommValue,"AmountStr",20,false))
                    {
                        sbError.append("Invalid "+dCommName+" Value<BR>");
                    }
                    else
                    {
                        dynamicInputValues.put(id,dCommValue);
                    }
                }
                else
                {
                    if (!ESAPI.validator().isValidInput(dCommName,dCommValue,"Numbers",20,false))
                    {
                        sbError.append("Invalid "+dCommName+" Value<BR>");
                    }
                    else
                    {
                        dynamicInputValues.put(id,dCommValue);
                    }
                }
            }
        }
        List<CommissionVO> commissionVOList=null;
        try
        {
            commissionVOList=commissionManager.getGatewayAccountDynamicInputCommissions(accountId);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException:::::"+e);
            request.setAttribute("commissionVOList",commissionVOList);
            request.setAttribute("statusMsg","Internal error while processing your request");
            rd.forward(request, response);
            return;
        }

        if (sbError.length() > 0)
        {
            request.setAttribute("commissionVOList",commissionVOList);
            request.setAttribute("statusMsg", sbError.toString());
            rd.forward(request, response);
            return;
        }
        //System.out.println("dynamicInputValues===" + dynamicInputValues);

        CommonFunctionUtil commonFunctionUtil=new CommonFunctionUtil();
        String effectiveStartDate=commonFunctionUtil.convertDatepickerToTimestamp(request.getParameter("firstdate"), request.getParameter("settledstarttime"));
        String effectiveEndDate=commonFunctionUtil.convertDatepickerToTimestamp(request.getParameter("lastdate"),request.getParameter("settledendtime"));

        String message= commonFunctionUtil.newValidateDate1(request.getParameter("firstdate") + " " + request.getParameter("settledstarttime"), request.getParameter("lastdate")+" "+request.getParameter("settledendtime"), null, null);
        //System.out.println("message======"+message);
        if(message!=null)
        {
            request.setAttribute("commissionVOList",commissionVOList);
            request.setAttribute("statusMsg", message);
            rd.forward(request, response);
            return;
        }
        try
        {
            ISOCommissionGenerator isoCommissionGenerator=new ISOCommissionGenerator();
            DateVO dateVO=new DateVO();

            TransactionManager transactionManager=new TransactionManager();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String firstTransactionDateOnAccount="";
            if(gatewayAccount==null)
            {
                request.setAttribute("commissionVOList",commissionVOList);
                request.setAttribute("statusMsg", "Gateway account not found");
                rd.forward(request, response);
                return;
            }

            firstTransactionDateOnAccount=transactionManager.getMemberFirstTransactionDateOnGatewayAccount(gatewayAccount);
            if(!functions.isValueNull(firstTransactionDateOnAccount))
            {
                request.setAttribute("commissionVOList",commissionVOList);
                request.setAttribute("statusMsg", "Transactions not found on gateway account");
                rd.forward(request, response);
                return;
            }
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ISOCommReportVO lastISOCommReportVO=commissionManager.getLastISOCommWireReport(gatewayAccount);
            if(lastISOCommReportVO!=null)
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(targetFormat.parse(lastISOCommReportVO.getEndDate()));
                cal.add(Calendar.SECOND, 1);
                effectiveStartDate=targetFormat.format(cal.getTime());
            }
            else
            {
                effectiveStartDate=targetFormat.format(targetFormat.parse(firstTransactionDateOnAccount));
            }

            List<CommissionVO> commissionVOListToProcess=commissionManager.getGatewayAccountCommission(accountId);
            if(commissionVOListToProcess==null || commissionVOListToProcess.size()<1)
            {
                request.setAttribute("commissionVOList",commissionVOList);
                request.setAttribute("statusMsg", "Commission not found on gateway account");
                rd.forward(request, response);
                return;
            }

            dateVO.setStartDate(effectiveStartDate);
            dateVO.setEndDate(effectiveEndDate);

            boolean pendingTransaction=transactionManager.checkPendingTransaction(gatewayAccount,dateVO);
            if(pendingTransaction){
                request.setAttribute("commissionVOList",commissionVOList);
                request.setAttribute("statusMsg", "Transaction status need to be corrected.");
                rd.forward(request, response);
                return;
            }

            ISOCommReportVO isoCommReportVO=isoCommissionGenerator.generateISOCommissionReport(accountId,dateVO,dynamicInputValues,gatewayAccount,commissionVOListToProcess);
            String result="";
            if(isoCommReportVO!=null)
            {
                isoCommReportVO.setActionExecutor(user.getAccountName());
                result = addNewISOCommissionWireNew(isoCommReportVO);
            }
            else
            {
                result="Wire Creation Failed";
            }
            request.setAttribute("commissionVOList",commissionVOList);
            request.setAttribute("statusMsg",result);
        }
        catch (Exception e)
        {
            log.error("Exception  ::::" + e.getMessage());
            request.setAttribute("statusMsg","Internal error while processing your request");
        }
        rd.forward(request,response);
        return;
    }
    public String addNewISOCommissionWireNew(ISOCommReportVO isoCommReportVO)throws SQLException,SystemError
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        String statusMsg="";
        try
        {
            conn= Database.getConnection();
            String query="insert into iso_commission_wire_manager(iso_comm_id,accountid,currency,startdate,enddate,status,reportfilepath,transactionfilepath,actionexecutor,creationdate,amount,netfinalamount,unpaidamount) values(null,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            pstmt=conn.prepareStatement(query);
            pstmt.setString(1,isoCommReportVO.getAccountId());
            pstmt.setString(2,isoCommReportVO.getCurrency());
            pstmt.setString(3,isoCommReportVO.getStartDate());
            pstmt.setString(4,isoCommReportVO.getEndDate());
            pstmt.setString(5,isoCommReportVO.getStatus());
            pstmt.setString(6,isoCommReportVO.getReportFilePath());
            pstmt.setString(7,isoCommReportVO.getTransactionFilePath());
            pstmt.setString(8,isoCommReportVO.getActionExecutor());
            pstmt.setDouble(9, isoCommReportVO.getAmount());
            pstmt.setDouble(10, isoCommReportVO.getNetfinalamount());
            pstmt.setDouble(11, isoCommReportVO.getUnpaidAmount());
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                statusMsg="Wire has been added successfully.";
            }
            else
            {
                statusMsg="Wire adding failed.";
            }
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return statusMsg;
    }
}


