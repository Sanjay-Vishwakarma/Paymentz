<%@ page language="java" import="java.net.*"%>

<%
		int noofpagesperblock=10;
		
		int currentnoofrecords=Integer.parseInt(request.getParameter("numrecords"));
		int currentnoofrecordsperpage=Integer.parseInt(request.getParameter("numrows"));
		//int currentnoofrecordsperpage=10;
		
		if(currentnoofrecordsperpage==0)
		{
			currentnoofrecordsperpage=30;
		}

		int currentpageno=Integer.parseInt(request.getParameter("pageno"));
		String str=request.getParameter("str");
		String querystr=str;
		String linkpage=request.getParameter("page");
		
		String pageorderby=request.getParameter("orderby");



////////// for calculating no of blocks
		
		int noofblocks=0;
		
		int blocksremainder=currentnoofrecords%(currentnoofrecordsperpage * noofpagesperblock);
		int blocksdivisor=(int)Math.floor(currentnoofrecords/(currentnoofrecordsperpage * noofpagesperblock));
		
		if(blocksremainder==0)
		{
			noofblocks=blocksdivisor;
		}
		else
		{
			noofblocks=blocksdivisor+1;
		}
		
		int currentblock=Integer.parseInt(request.getParameter("currentblock"));
		

////////// for calculating no of pages
		int totalnofopages=0;
		int remainder=currentnoofrecords%currentnoofrecordsperpage;
		int divisor=(int)Math.floor(currentnoofrecords/currentnoofrecordsperpage);
		
		if(remainder==0)
		{
			totalnofopages=divisor;
		}
		else
		{
			totalnofopages=divisor+1;
		}
		

///////checking page no
////this code cshould always be above the code for checking current variable


	if(currentpageno>totalnofopages)	
	{
		currentpageno=totalnofopages;
	}

	if(currentpageno<1)	
	{
		currentpageno=1;
	}


//////////////chcking the currentblock variable

	int	tempcurrentblock=currentpageno / noofpagesperblock;
	
		if(currentpageno%noofpagesperblock!=0)
		{
			currentblock=tempcurrentblock+1;
		}
		else
		{
			currentblock=tempcurrentblock;
		}





//		String str="SName="+name+"&SState="+state+"&SCity="+city+"&SZip="+zip+"&SCountry="+country+"&SStatus="+commaseperatedownerstatus+"&SRec="+records+"&SOrderby="+commaseperatedorderby;
		String tempstr="";

		int nextblock=currentblock+1;
		int prevblock=currentblock-1;
		
		if(prevblock==0)
		{
			prevblock=1;
		}
		
		if(nextblock==0)
		{
			nextblock=1;
		}
		
int nextblockpageno=currentblock*noofpagesperblock+1;
int prevblockpageno=((currentblock-2)*noofpagesperblock)+1;

int tempprevcurrentblock=currentblock;
int tempnextcurrentblock=currentblock;

/*
out.println("currentnoofrecords= "+currentnoofrecords);	
out.println("currentnoofrecordsperpage= "+currentnoofrecordsperpage);	
out.println("noofblocks= "+noofblocks);			
out.println("currentblock= "+currentblock);	
out.println("nextblock= "+nextblock);
out.println("prevblock= "+prevblock);
out.println("nextblockpageno= "+nextblockpageno);
out.println("prevblockpageno= "+prevblockpageno);
*/
//out.println("currentpageno= "+currentpageno);

//out.println(querystr+"<br><br>");
%>

<form action="<%=linkpage%>?<%=querystr%>&currentblock=<%=currentblock%>" method=post>

<%
		if(currentpageno>1 && currentblock>1)
		{
			if(!pageorderby.equals("null"))
			{
				tempstr= str +"&SOrderby="+pageorderby + "&SPageno=" +1 +"&currentblock=" +1;
			}	
			else
			{
				tempstr= str + "&SPageno=" +1 +"&currentblock=" +1;
			}	
			
			out.println("<a href=\""+linkpage+"?" + tempstr +"\"><img src=/merchant/images/first.gif border=0></a>");
		//	out.println(" | ");
		}
		else
		{
			out.println("<img src=/merchant/images/first1.gif border=0>");
		//	out.println(" | ");
		}



		if(currentblock>1)
		{
			if(!pageorderby.equals("null"))
			{
			tempstr= str +"&SOrderby="+pageorderby + "&SPageno=" + prevblockpageno +"&currentblock=" +prevblock;
			}
			else
			{
			tempstr= str + "&SPageno=" + prevblockpageno +"&currentblock=" +prevblock;
			}
			out.println("<a href=\""+linkpage+"?" + tempstr +"\"><img src=/merchant/images/previous.gif border=0></a>");
		//	out.println(" | ");
		}
		else
		{
			out.println("<img src=/merchant/images/previous1.gif border=0>");
		//	out.println(" | ");
		}
		

	for(int i=1;i<=noofpagesperblock;i++)
	{

		int temppageno = ((currentblock-1) * noofpagesperblock)+i;
		
//		out.println("temppageno= "+temppageno);
		
//		out.println("totalnofopages= "+totalnofopages);
		
		if(temppageno<=totalnofopages)	
		{	
			
			if(temppageno == currentpageno)
			{
				out.println("<span class=\"nolink\">"+temppageno+"</span>");
			}
			else
			{
			tempstr="";
			
			if(!pageorderby.equals("null"))
			{
				tempstr=str+"&SOrderby="+pageorderby +"&SPageno="+ temppageno +"&currentblock="+ currentblock;
			}
			else
			{
				tempstr=str +"&SPageno="+ temppageno +"&currentblock="+ currentblock;
			}
			
			
			//	out.println("<a href=\"javascript:loadpage("+i+")\" >"+ i +"</a>");
			//out.println("<a href=\"Owner.jsp?SPageno="+ i + "& \" >"+ i +"</a>");
			out.println("<a href=\""+linkpage+"?" + tempstr + " \" class=\"link\">" + "<font size=1 face=\"verdana,arial\">" + temppageno +"</font></a>");
			}
		}	
	}

		if(currentblock<noofblocks)
		{
		//out.println(" | ");
		tempstr="";
			if(!pageorderby.equals("null"))
			{
				tempstr=str+"&SOrderby="+pageorderby +"&SPageno="+ nextblockpageno +"&currentblock="+nextblock;
			}
			else
			{
				tempstr=str+"&SPageno="+ nextblockpageno +"&currentblock="+nextblock;
			}
		
		out.println("<a href=\""+linkpage+"?" + tempstr +" \"><img src=/merchant/images/next.gif border=0></a>");
		}
		else
		{
			out.println("<img src=/merchant/images/next1.gif border=0>");
		//	out.println(" | ");
		}

		if(currentpageno!=totalnofopages && currentblock!=noofblocks)
		{
			if(!pageorderby.equals("null"))
			{
				tempstr= str +"&SOrderby="+pageorderby + "&SPageno=" +totalnofopages +"&currentblock=" +noofblocks;
			}
			else
			{
				tempstr= str + "&SPageno=" +totalnofopages +"&currentblock=" +noofblocks;
			}

			
			out.println("<a href=\""+linkpage+"?" + tempstr +"\"><img src=/merchant/images/last.gif border=0></a>");
		//	out.println(" | ");
		}
		else
		{
			out.println("<img src=/merchant/images/last1.gif border=0>");
		//	out.println(" | ");
		}


%>
<!--</td><td nowrap colspan="3" align=left>-->
	
	<span class="textb">Page No:</span>&nbsp;
	<input type="text" name="SPageno" size=2 maxlength=4 class="textBoxes">
	&nbsp;&nbsp;
	<input type="submit" value="Go To" class="submit">
</form>


