import com.directi.pg.Admin;
import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RawCheck extends HttpServlet
{
    private static Logger logger = new Logger(RawCheck.class.getName());
    PreparedStatement prstmt = null;
    java.util.Date dt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    public synchronized void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if(Functions.validateCSRF(request.getParameter("ctoken"),user))
        {
            session.setAttribute("ESAPIUserSessionKey",user);
        }
        else
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        logger.debug("success");

        logger.debug("Entering in RawCheck");
        String query = null;
        String icicimerchantid = null;
        String pathtoraw = null;

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        String description = null;
        int result = 0;
        int count = 0;
        int toid = 0;
        int prevtoid = 0;
        int trackingId = 0;
        int paymentTransId = 0;
        BigDecimal peramt = null;

        int queryCount = 0;
        //int queryAmount=0;
        BigDecimal queryAmount = new BigDecimal("0");
        int totalCount = 0;
        PrintWriter out = response.getWriter();
        ServletContext application = getServletContext();
        response.setContentType("text/html");
        String EOL = "<BR>";
        String errormsg="";
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/rawbatch.jsp");
        RequestDispatcher rdError = request.getRequestDispatcher("/rawbatch.jsp?MES=ERR");

        try
        {
            validateMandatoryParameter(request);
        }
        catch (ValidationException ex)
        {
            logger.error("ICICI Merchanid Not Found",ex);
            return;
        }
        icicimerchantid = (String) application.getAttribute("MERCHANTID");

        pathtoraw = ApplicationProperties.getProperty("MPR_FILE_STORE");
        String filename = "R" + getBatchName() + ".raw";
        StringBuffer scsrec = new StringBuffer();
        StringBuffer rawdataBuf = new StringBuffer();

        Connection conn = null;
        FileUploadBean fub = new FileUploadBean();
        fub.setSavePath(pathtoraw);
        try
        {
            fub.doUpload(request, filename);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while FileUploadBean",systemError);
        }
        out.println("filename : " + fub.getFilename());

        try
        {
            BufferedReader rin = new BufferedReader(new FileReader(pathtoraw + fub.getFilename()));
            String temp = null;

            while ((temp = rin.readLine()) != null)
            {
                rawdataBuf.append(temp + "\r\n");
            }
            rin.close();
        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            logger.error("Raw File Not Found:::: Exception ",ex);
            errormsg = errormsg + "Raw File Not Found"+EOL;
            request.setAttribute("error",errormsg);
            rdError.forward(request, response);
            return;
        }
        String rawdata = rawdataBuf.toString();
/*
		String rawdata=request.getParameter("rawdata");

		if(rawdata==null)
		{
			out.println("Raw data not passed <br><br>");
			return;
		}
*/
        StringTokenizer stz = new StringTokenizer(rawdata, "\r\n");
        /*PreparedStatement prstmt = null;
        ResultSet rs = null;*/
        try
        {
            conn = Database.getConnection();
            prstmt = conn.prepareStatement("select status,captureamount from transaction_icicicredit where icicimerchantid=? and captureamount=? and capturereceiptno=? and authcode=?");
            //icicimerchantid='"+marchntid+"' and captureamount=" + amount.doubleValue() + " and capturereceiptno='" + captureRRN + "' and authcode='" + authCode + "' and status in ('podsent','settled')
            BigDecimal totalAmount = new BigDecimal("0");
            String successrecs = "", failrecs = "", setledrecs = "";
            while (stz.hasMoreElements())
            {
                String value = ((String) stz.nextElement()).trim();
                StringTokenizer innerStz = new StringTokenizer(value, "^");
                totalCount++;

                if (innerStz.hasMoreElements())
                {
                    //innerStz.nextElement();
                    String merchntid = ((String) innerStz.nextElement()).trim();
                    String date = ((String) innerStz.nextElement()).trim();
                    //int captureRRN=Integer.parseInt((String)innerStz.nextElement());
                    String captureRRN = ((String) innerStz.nextElement()).trim();
                    //captureRRN=captureRRN.substring(captureRRN.length()-6);
                    String authCode = ((String) innerStz.nextElement()).trim();
                    String tempamount = ((String) innerStz.nextElement()).trim();
                    BigDecimal amount = new BigDecimal(tempamount);
                    amount = amount.multiply(new BigDecimal("0.01"));
                    totalAmount = totalAmount.add(amount);

                    //query = "select status,captureamount from transaction_icicicredit where ";
                    //query += "icicimerchantid='"+merchntid+"' and captureamount=" + amount.doubleValue() + " and capturereceiptno='" + captureRRN + "' and authcode='" + authCode + "' and status in ('podsent','settled')";
                    prstmt.setString(1, merchntid);
                    prstmt.setDouble(2, amount.doubleValue());
                    prstmt.setString(3, captureRRN);
                    prstmt.setString(4, authCode);
                    //ResultSet rs = Database.executeQuery(query, conn);
                    rs = (ResultSet) prstmt.executeQuery();
                    if (rs.next())
                    {
                        if (rs.getString("status").equals("settled"))
                            setledrecs += value + "<br>";
                        else if (rs.getString("status").equals("podsent"))
                        {
                            queryCount++;
                            if (rs.getString("captureamount") != null)
                                queryAmount = queryAmount.add(new BigDecimal(rs.getString("captureamount")));
                            successrecs += value + "<br>";
                            scsrec.append(value + "\r\n");
                        }
                        else
                        {
                            failrecs += value + "<br>";
                        }
                    }
                    else
                    {
                        failrecs += value + "<br>";
                    }
                }//if ends
            }//while ends

            out.println("<br><br>");
            out.println("totalcount settled: " + totalCount);

            out.println("<br><br>");
            out.println("total Amount settled: " + totalAmount);

            out.println("<br><br>");

            out.println("totalcount captured: " + queryCount + "<br><br>");
            out.println("total Amount captured: " + queryAmount + "<br><br>");

            FileWriter fw = new FileWriter(pathtoraw + fub.getFilename());
            fw.write(scsrec.toString());
            fw.close();
            out.println("<br><br><b> Record(s) not matched for following MPRs </b><br>" + failrecs);
            out.println("<br><br> Record(s) matched for following MPRs  <br>" + successrecs);
            out.println("<br><br> Record(s) Allready settled for following MPRs <br>" + setledrecs);

            out.println("<script language=\"javascript\">function confirmbatch(){if(confirm(\"Do u really want to confirm the batch\")){document.f1.mybutton.disabled=true;return true;}return false;}\n\n function cancelbatch(){if(confirm(\"Do u really want to cancel the batch\")){document.f1.action=\"/icici/servlet/RawCancel\";document.f1.submit()};}</script>");
            out.println("<form name=f1 action=\"/icici/servlet/RawConfirmBatch?ctoken=\""+user.getCSRFToken()+"\" method=post onSubmit='return confirmbatch()'>");

            //if(queryCount==totalCount && totalAmount.compareTo(queryAmount)==0)
            if (!successrecs.equals(""))
                out.println("<input type=submit name=mybutton value=settle>");

            out.println("<input type=hidden name=filename value=\"" + fub.getFilename() + "\">");
            out.println("<input type=hidden name=pathtoraw value=\"" + pathtoraw + "\">");
            out.println("<input type=button name=cancel value=Cancel onclick='cancelbatch()'>");
            out.println("</form>");
//				out.println("<input type=hidden name=query value=\""+queryold+"\">");
            rdSuccess.forward(request, response);

        }
        catch (SystemError se)
        {    logger.error("System Error in RawCheck::::",se);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
        }
        catch (Exception e)
        {   logger.error("Exception :::::",e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            errormsg = errormsg + "System Error in RawCheck1"+EOL;
            request.setAttribute("error",errormsg);
            rdError.forward(request, response);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(prstmt);
            Database.closeConnection(conn);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        service(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        service(request, response);
    }//post ends

    private String getBatchName()
    {
        return "" + System.currentTimeMillis() / 1000;
    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.MERCHANTID_CAPS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
