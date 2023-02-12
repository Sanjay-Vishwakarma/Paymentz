import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.directi.pg.*;
import java.sql.*;

public class FinanceAccounts extends HttpServlet
{

	static Logger log = new Logger(FinanceAccounts.class.getName());	

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
			doPost(request,response);
    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException
    {
		HttpSession session=req.getSession();	

		/*if(session.getAttribute("login")!=null)
		{	
			res.sendRedirect("/newfinance/logout.jsp");
			return;
		}*/

		String merchantid=req.getParameter("merchantid");

		ServletContext application=getServletContext();

		int start=0; // start index
		int end=0; // end index
		String str=null;

		String desc=Functions.checkStringNull(req.getParameter("desc"));
		String fdate=Functions.checkStringNull(req.getParameter("fdate"));
		String tdate=Functions.checkStringNull(req.getParameter("tdate"));
		String fmonth=Functions.checkStringNull(req.getParameter("fmonth"));
		String tmonth=Functions.checkStringNull(req.getParameter("tmonth"));
		String fyear=Functions.checkStringNull(req.getParameter("fyear"));
		String tyear=Functions.checkStringNull(req.getParameter("tyear"));

		Calendar rightNow = Calendar.getInstance();

		if(fdate==null)fdate=""+1;
		if(tdate==null)tdate=""+rightNow.get(rightNow.DATE);

		if(fmonth==null)fmonth=""+ rightNow.get(rightNow.MONTH);
		if(tmonth==null)tmonth=""+ rightNow.get(rightNow.MONTH);

		if(fyear==null)fyear=""+rightNow.get(rightNow.YEAR);
		if(tyear==null)tyear=""+rightNow.get(rightNow.YEAR);


		//log.info("fmonth "+ fmonth);
		//log.info("tmonth "+ tmonth);

		String fdtstamp=Functions.converttomillisec(fmonth,fdate,fyear,"0","0","0");
		String tdtstamp=Functions.converttomillisec(tmonth,tdate,tyear,"23","59","59");

		res.setContentType("text/html");
		req.setAttribute("fdtstamp",fdtstamp);
		req.setAttribute("tdtstamp",tdtstamp);

		PrintWriter out = res.getWriter();

		//Functions fn=new Functions();
		int pageno=Functions.convertStringtoInt(req.getParameter("SPageno"),1);
		int records=Functions.convertStringtoInt(req.getParameter("SRecords"),30);
	
		try
		{

			TransactionEntry transactionentry= new TransactionEntry(Integer.parseInt(merchantid));
			Hashtable hash=transactionentry.listAccounts(desc,tdtstamp,fdtstamp,records,pageno);
			
			req.setAttribute("Accountsdetails",hash);
			req.setAttribute("balance",transactionentry.getBalance());
		 
			//int totalrecords=Integer.parseInt((String)hash.get("records"));
			//Hashtable temphash=(Hashtable)hash.get(""+totalrecords);

			int totalrecords=Integer.parseInt((String)hash.get("records"));
			
			if(totalrecords==0)
			{
				req.setAttribute("cfbalance",transactionentry.getSpecialBalance(fdtstamp,0));
			}
			else
			{
				Hashtable temphash=(Hashtable)hash.get(""+1);
				int transid=Integer.parseInt((String)temphash.get("transid"));
				String newdtstamp=(String)temphash.get("dtstamp");
				req.setAttribute("cfbalance",transactionentry.getSpecialBalance(newdtstamp,transid));
			}
			

			RequestDispatcher rd=req.getRequestDispatcher("/financeaccounts.jsp");
			rd.forward(req,res);
	
		}
		catch(SystemError se)
		{
			StringWriter sw=new StringWriter();
			se.printStackTrace(new PrintWriter(sw));
			out.println(Functions.ShowMessage("Error",sw.toString()));		
		}
		catch(Exception e)
		{
			StringWriter sw=new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			out.println(Functions.ShowMessage("Error",sw.toString()));
		}
	}
}
