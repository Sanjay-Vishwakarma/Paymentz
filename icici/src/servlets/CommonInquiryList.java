import com.directi.pg.Admin;
import com.directi.pg.*;
import com.directi.pg.core.GatewayTypeService;
import com.manager.TransactionManager;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/5/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonInquiryList extends HttpServlet
{
    private static Logger log = new Logger(CommonInquiryList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        log.debug("Entering in CommonInquiryList");
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        TransactionVO transactionVO = new TransactionVO();
        TransactionManager transactionManager = new TransactionManager();
        Functions functions = new Functions();
        PaginationVO paginationVO = new PaginationVO();
        log.debug("ctoken==="+req.getParameter("ctoken"));
        Connection conn = null;
        String errormsg= "";
        String EOL = "<BR>";
        int records=15;
        int pageno=1;
        Hashtable hash = null;
        Hashtable statushash=null;
        String sendmail="";
        Hashtable transdetail=null;
        List<TransactionVO> inquiryList=null;
        String gateway = "";
        String currency = "";
        String toid = "";
        String accountid = "";

        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input", e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+e.getMessage()+ "</b></font></center>";
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/commoninquirylist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        if (req.getParameter("toid")!=null && !req.getParameter("toid").equalsIgnoreCase("0"))
        {
            toid = req.getParameter("toid");
            req.setAttribute("toid", toid);
        }
        String paymentid= req.getParameter("paymentid");
        req.setAttribute("paymentid",paymentid);

        String trackingid= req.getParameter("trackingid");

        req.setAttribute("trackingid",trackingid);

        String description= req.getParameter("orderid");
        req.setAttribute("description",description);

        if (req.getParameter("accountid")!=null &&!req.getParameter("accountid").equalsIgnoreCase("0"))
        {
            accountid = req.getParameter("accountid");
            req.setAttribute("accountid", accountid);
        }

        String pgtypeid = req.getParameter("pgtypeid");
        req.setAttribute("pgtypeid",pgtypeid);
        if (req.getParameter("pgtypeid")!=null &&pgtypeid.split("-").length == 3 && !pgtypeid.equalsIgnoreCase("0"))
        {
            gateway = pgtypeid.split("-")[0];
            currency = pgtypeid.split("-")[1];
        }

        String status = req.getParameter("status");
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");

        Calendar rightNow = Calendar.getInstance();
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);

        StringBuffer trackingIds=new StringBuffer();
        if (functions.isValueNull(trackingid))
        {
            List<String> trackingidList = null;
            if(trackingid.contains(","))
            {
                trackingidList = Arrays.asList(trackingid.split(","));
            }
            else
            {
                trackingidList = Arrays.asList(trackingid.split(" "));
            }
            int i = 0;
            Iterator itr = trackingidList.iterator();
            while (itr.hasNext())
            {
                if(i!=0)
                {
                    trackingIds.append(",");
                }
                trackingIds.append(""+itr.next()+"");
                i++;
            }
        }

        // for description
        StringBuffer orderIds= new StringBuffer();
        if (functions.isValueNull(description))
        {
            List<String> orderidList= null;
            if (description.contains(","))
            {
                orderidList= Arrays.asList(description.split(","));
            }
            else
            {
                orderidList= Arrays.asList(description.split(" "));
            }
            for ( int j=0; j<orderidList.size();j++)
            {
                if (j!=0)
                {
                    orderIds.append(",");
                }
                String listvalue= orderidList.get(j);
                if (functions.isValueNull(listvalue) && listvalue.startsWith("'") && listvalue.endsWith("'"))
                {
                    orderIds.append(""+orderidList.get(j)+"");
                }
                else
                {
                    orderIds.append("'"+orderidList.get(j)+"'");
                }
            }
        }
        //int start = 0; // start index
        //int end = 0; // end index

        /*pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;*/

        transactionVO.setStatus(status);
        transactionVO.setToid(toid);
        transactionVO.setPgtypeid(gateway);
        transactionVO.setCurrency(currency);
        transactionVO.setTrackingId(trackingIds.toString());
        transactionVO.setPaymentId(paymentid);
        transactionVO.setAccountId(accountid);
        transactionVO.setOrderDesc(orderIds.toString());

        paginationVO.setInputs("toid="+transactionVO.getToid()+"&trackingid="+transactionVO.getTrackingId()+"&orderid="+transactionVO.getOrderDesc()+"&fdate="+fdate+"&fmonth="+fmonth+"&fyear="+fyear+"&tdate="+tdate+"&tmonth="+tmonth+"&tyear="+tyear);
        paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
        paginationVO.setPage(CommonInquiryList.class.getName());
        paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
        paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));

        /*hash = transactionManager.getTransactionListForCommonInquiry(transactionVO,fdtstamp,tdtstamp,String.valueOf(start),String.valueOf(end));
        req.setAttribute("transdetails", hash);*/
        inquiryList = transactionManager.getTransactionListForInquiry(transactionVO,fdtstamp,tdtstamp,paginationVO);
        req.setAttribute("transdetails", inquiryList);
        req.setAttribute("trackingid",trackingid);
        req.setAttribute("paginationVO",paginationVO);
        RequestDispatcher rd = req.getRequestDispatcher("/commoninquirylist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    public Set<String> getGatewayHash(String gateway)
    {
        Set<String> gatewaySet = new HashSet<String>();

        if(gateway==null || gateway.equals("") || gateway.equals("null"))
        {
            gatewaySet.addAll(GatewayTypeService.getGateways());
        }
        else
        {

            gatewaySet.add(GatewayTypeService.getGatewayType(gateway).getGateway());
        }
        return gatewaySet;
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        /*inputFieldsListOptional.add(InputFields.TOID);*/
        inputFieldsListOptional.add(InputFields.PAYMENTID);
        inputFieldsListOptional.add(InputFields.ORDERID);
        inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        // inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.STATUS);
        //inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_SMALL);
//        inputFieldsListMandatory.add(InputFields.PAYMENTID);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
    public boolean isValueNull(String str)
    {
        if(str != null && !str.equals("null") && !str.equals(""))
        {
            return true;
        }
        return false;
    }
}
