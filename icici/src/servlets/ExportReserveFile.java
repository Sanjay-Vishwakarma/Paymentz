import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.manager.TransactionManager;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: WAHEED
 * Date: 6/5/14
 * Time: 8:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExportReserveFile extends HttpServlet
{
    static Logger log = new Logger(ExportReserveFile.class.getName());
    private Connection conn = null;
    private int index;
    private ResultSet rs =null;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Functions functions = new Functions();
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        String accountid = "";
        String fromDate = request.getParameter("fromDate");
        String startTime="00:00:00";
        String endTime = "23:59:59";
        int records=15;
        int pageno=1;
        String gateway = "";
        String currency = "";
        int start = 0; // start index
        int end = 0; // end index

        String error="";

        if(error.length() > 0)
        {
            request.setAttribute("error",error);
            RequestDispatcher rd = request.getRequestDispatcher("/generateRollingReserveFile.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }

        error = error + validateForPageNoParameters(request);
        accountid = request.getParameter("accountid");
        if(error.length() > 0)
        {
            request.setAttribute("error",error);
            RequestDispatcher rd = request.getRequestDispatcher("/generateRollingReserveFile.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }

        if(functions.isValueNull(request.getParameter("gateway")) && !"0".equals(request.getParameter("gateway")))
        {
            String gatewayArr[] = request.getParameter("gateway").split("-");
            gateway = gatewayArr[0];
            currency = gatewayArr[1];
        }

        /*if (gateway.equals("0") || gateway.equals(""))
        {
            error = error+"Kindly provide valid Gateway";
        }*/

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        String toDate = request.getParameter("toDate");
        request.setAttribute("accountid",accountid);
        request.setAttribute("fromDate",fromDate);
        request.setAttribute("toDate",toDate);
        if(fromDate==null || ("").equals(fromDate))
        {
            fromDate = "01/01/2005";
        }
        if(toDate==null || ("").equals(toDate))
        {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            toDate = dateFormat.format(date);
        }
        Hashtable hash = null;
        final String OLD_FORMAT = "dd/MM/yyyy";
        final String NEW_FORMAT = "yyyy/MM/dd";
        String oldDateString = fromDate;
        String oldDateString2 = toDate;
        String newDateString;
        String newDateString2;
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        Date d2 = null;
        Hashtable transactionHash = null;
        Hashtable timeDifferenceHash = null;
        try
        {
            d = sdf.parse(oldDateString);
            d2 = sdf.parse(oldDateString2);
            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(d);
            newDateString2= sdf.format(d2);
            timeDifferenceHash = getTimeDetails(accountid);
            //rs.next();
            String time_difference_normal="00:00:00";
            String time_difference_daylight="00:00:00";
            if(timeDifferenceHash!=null)
            {
                time_difference_normal = timeDifferenceHash.containsKey(1)?((((Hashtable)timeDifferenceHash.get(1)).containsKey("time_difference_normal"))?(((Hashtable)timeDifferenceHash.get(1)).get("time_difference_normal").toString()):"00:00:00"):"00:00:00";
                time_difference_daylight = timeDifferenceHash.containsKey(1)?((((Hashtable)timeDifferenceHash.get(1)).containsKey("time_difference_daylight"))?(((Hashtable)timeDifferenceHash.get(1)).get("time_difference_daylight").toString()):"00:00:00"):"00:00:00";
            }
            request.setAttribute("time_difference_normal",time_difference_normal);
            request.setAttribute("time_difference_daylight",time_difference_daylight);
            if(request.getParameter("time_difference_daylight").equalsIgnoreCase("Y"))
            {
                request.setAttribute("isDaylight","Y");
                startTime = time_difference_daylight;
                endTime = time_difference_daylight;
            }
            else
            {
                startTime = time_difference_normal;
                endTime = time_difference_normal;
            }
            TransactionManager transactionManager = new TransactionManager();
            String view = request.getParameter("mybutton");
            hash =transactionManager.listTransaction(accountid, gateway, currency, newDateString, newDateString2, startTime, endTime, view, pageno, records);
            int totalRecords = Integer.parseInt((String) hash.get("records"));

            if(("View").equals(request.getParameter("mybutton")))
            {
                request.setAttribute("transdetails", hash);
                request.setAttribute("error", error);
                RequestDispatcher rd = request.getRequestDispatcher("/generateRollingReserveFile.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                request.setAttribute("mybutton",request.getParameter("mybutton"));
            }
            else
            {
                if(totalRecords>0)
                {
                    String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
                    String fileName = "ReserveFileReport.xls";
                    HSSFWorkbook wb = new HSSFWorkbook();
                    HSSFSheet sheet = wb.createSheet("Excel Sheet");
                    HSSFRow row = sheet.createRow((short)index);
                    row.createCell((short)0).setCellValue("Tracking Id");
                    row.createCell((short)1).setCellValue("Description");
                    row.createCell((short)2).setCellValue("Account Id");
                    row.createCell((short)3).setCellValue("Amount");
                    row.createCell((short)4).setCellValue("Rolling Amount");
                    index=index+1;
                    for (int pos = 1; pos <= totalRecords; pos++)
                    {
                        transactionHash = (Hashtable) hash.get(pos + "");
                        row = sheet.createRow((short)index);
                        row.createCell((short)0).setCellValue((String) transactionHash.get("trackingid"));
                        row.createCell((short)1).setCellValue((String) transactionHash.get("description"));
                        row.createCell((short)2).setCellValue((String) transactionHash.get("accountid"));
                        row.createCell((short)3).setCellValue((String) transactionHash.get("amount"));
                        row.createCell((short)4).setCellValue((String) transactionHash.get("RollingReserveAmountKept"));
                        index++;
                    }
                    index=0;
                    FileOutputStream fileOut = new FileOutputStream(exportPath+"/"+fileName);
                    wb.write(fileOut);
                    fileOut.close();
                    sendFile(exportPath+"/" + fileName, fileName, response);
                }
                else
                {
                    request.setAttribute("transdetails",hash);
                    request.setAttribute("error", error);
                    RequestDispatcher rd = request.getRequestDispatcher("/generateRollingReserveFile.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                }
            }
        }
        catch (ParseException e)
        {
            log.error("parsing Exception while export file local path",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {
            log.error("Exception while ExportReserveFile",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        RequestDispatcher rd = request.getRequestDispatcher("/generateRollingReserveFile.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request, response);
    }

    public static boolean sendFile(String filepath, String filename, HttpServletResponse response)throws Exception
    {
        File f = new File(filepath);
        int length = 0;
        response.setContentType("application/vnd.ms-excel");
        response.setContentLength((int) f.length());
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        javax.servlet.ServletOutputStream op = response.getOutputStream();
        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            op.write(bbuf, 0, length);
        }
        in.close();
        op.flush();
        op.close();
        File file = new File(filepath);
        file.delete();
        log.info("Successful#######");
        return true;
    }

    private Hashtable getTimeDetails(String accountid)
    {
        try
        {
            conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement("select time_difference_normal, time_difference_daylight from gateway_type where pgtypeid=(select pgtypeid from gateway_accounts where accountid ="+accountid+")");
            rs = ps.executeQuery();

            return Database.getHashFromResultSet(rs);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError while getting time details",systemError);

        }
        catch (SQLException e)
        {
            log.error("SQL Exception",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return null;
    }
    /*private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.PGTYPEID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,false);

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
    }*/

    private String validateForPageNoParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);
        inputFieldsListOptional.add(InputFields.FROMDATE);
        inputFieldsListOptional.add(InputFields.TODATE);

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
}