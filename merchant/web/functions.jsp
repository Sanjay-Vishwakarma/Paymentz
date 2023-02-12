<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.StringTokenizer" %><%!
//checks the strting and returns null if blank
public String checkStringNull(String checkstr)
{
	if( checkstr!=null)	
	{	
	checkstr=checkstr.trim();
	
		if(checkstr.equals("null"))
		{
		checkstr="";
		}
		
		if(checkstr.equals(""))
		{
		checkstr=null;
		}
		
	}	
	return checkstr;
}

public String checkValue(String checkstr)
{
	if(checkstr!=null)	
	{	
		checkstr=checkstr.trim();
		if(checkstr.equals("null") || checkstr.equals(""))
		{
		checkstr="-";
		}
	}
	else
	{
		checkstr="-";
	}
	return checkstr;
}

public String checkTelFax(String cc,String no)
{
	String str="";
	if(cc!=null && no!=null)	
	{
		str=cc + "-" + no;
	}
	else
	{
		str="-";
	}
	return str;
}


//checks the strting and returns null if blank
public String[] checkArrayNull(String[] checkarr)
{
		
	if(checkarr!=null)
	{
		if(checkarr.length ==0 ) //if the user deselect the any option
		{
			checkarr=null;
		}
		else if(checkarr[0].equals(""))
		{
			checkarr=null;
		}
	}	
return checkarr;
}

//converts strtig to int if string is "" then returns default value passed to it

public int convertStringtoInt(String convertstr,int defaultval)
{
	int	val=defaultval;
	
	if(convertstr!=null)  
	{
		convertstr=convertstr.trim();
		
		if(!convertstr.equals(""))
		{
			try
			{
				val=Integer.parseInt(convertstr);
			}
			catch(NumberFormatException nfe) 
			{
				val=defaultval;	
			}	
		}
		
	}
	return val;
}

//converts string array to commaseperated string

public String commaseperatedString(String[] arr)
{
	String commastr="";
	
	if(arr!=null)
	{
		if(arr.length !=0 ) //if the user deselect the any option
		{
			for(int z=0;z<arr.length;z++)
			{
				if(z!=arr.length-1)
				{
					if(!arr[z].equals(""))
					{
						commastr=commastr+arr[z]+",";
					}	
				}
				else
				{
					
					commastr=commastr+arr[z];
	
				}	
			}
		}	
	}	
	return commastr;
}

//converts mm/dd/yy to millisec

public String converttomillisec(String tempmm,String tempdd,String tempyy)
{
long tempdt=0;
String strdt=null;

if(tempmm !=null && tempdd != null && tempyy !=null)
{
	tempmm=tempmm.trim();
	tempdd=tempdd.trim();
	tempyy=tempyy.trim();
	
	if(tempmm.equals("") || tempdd.equals("") || tempyy.equals(""))
	{
    	tempdt=0;
//		out.println("mm "+tempmm +" : " +"dd "+dd +" : " +"yy "+yy +"<br>" );
	}
	else
	{
		try
		{
			//Date dt =new Date(Integer.parseInt(tempyy),Integer.parseInt(tempmm),Integer.parseInt(tempdd));
			//tempdt=dt.getTime();
			
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(tempyy),Integer.parseInt(tempmm),Integer.parseInt(tempdd));
			tempdt=cal.getTime().getTime();
			tempdt=tempdt/1000;
			strdt=""+tempdt;
		}
		catch(NumberFormatException nfe)
		{
			strdt=null;
		}
	}
	
}	

return strdt;

}

//converts strtig array to int arr

public int[] convertStringarrtoIntarr(String convertstrarr[])
{
	int val[]=null;
	
	if(convertstrarr!=null)
	{
		if(convertstrarr.length!=0)
		{
			val=new int[convertstrarr.length];
			
			for(int i=0;i<convertstrarr.length;i++)	
			{
				try
				{
					val[i]=Integer.parseInt(convertstrarr[i]);
				}
				catch(NumberFormatException nfe) 
				{
					val=null;
				}
			}
		}	
	}		
	return val;
}

/*
public int[] convertStringarrtoIntarr(String convertstrarr[])
{
	int val[]=null;
	
	if(convertstrarr!=null)
	{
		if(convertstrarr.length!=0)
		{
			val=new int[convertstrarr.length];
			
			for(int i=0;i<convertstrarr.length;i++)	
			{
				val[i]=Integer.parseInt(convertstrarr[i]);
			}
		}	
	}		
	return val;
}
*/
//converts commaseperated string to int array

public int[] convertCommaseperatedStringtoIntarr(String commastr)
{
	int val[]=null;

	if(commastr!=null)
	{
		commastr=commastr.trim();
				
		if(!commastr.equals(""))
		{
		   //if(commastr.indexOf(",")!=-1)
		   //{
				StringTokenizer stz=new StringTokenizer(commastr,",");
				val=new int[stz.countTokens()];				
				
				int pos=0;
				while(stz.hasMoreTokens())
				{
					String tempstr=stz.nextToken();
					
				//	if(tempstr!=null && !tempstr.equals("null"))
				//		{
						try
						{
							val[pos]=Integer.parseInt(tempstr);
							pos++;
						}
						catch(NumberFormatException nfe)
						{
							val[pos]=0;
							pos++;
						}
				//	}	
				//}
			}	
		}	
	}
	else
	{
		val=null;
	}		
	
	return val;
}

public String[] convertCommaseperatedStringtoStringarr(String commastr)
{
	String val[]=null;

	if(commastr!=null)
	{
		commastr=commastr.trim();
				
		 if(!commastr.equals(""))
		 {
		
				StringTokenizer stz=new StringTokenizer(commastr,",");
				val=new String[stz.countTokens()];				
				
				int pos=0;
				while(stz.hasMoreTokens())
				{
					String tempstr=stz.nextToken();
					
					val[pos]=tempstr;
					pos++;
				}		
				
		}	
	}
	else
	{
		val=null;
	}		
	
	return val;
}


//returns a string of concated option tag

public String printoptions(int start,int end)
{
	String str="";
	
	for(int i=start;i<=end;i++)
	{
		str=str+"<option value=" + i +">"+ i +"</option>";
	}
	return str;	
}

public String monthoptions(int start,int end)
{
	String str="";
	String month[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	
	for(int i=start;i<=end;i++)
	{
		str=str+"<option value=" + i +">"+month[i] +"</option>";
	}
	return str;
}

/*
public Calender getCalender(String timestamp)
{
	java.util.GregorianCalendar cal=new java.util.GregorianCalendar();
	cal.setTime(new java.util.Date(Long.parseLong(timestamp+"000")));
	return cal;
}
*/

public String NoRecordsFound()
{
	String str="";
	str=str+"<br><br><br>";
	str=str+"<table border=1  cellpadding=2 cellspacing=0 height=30% width=50% bordercolorlight=#000000 bordercolordark=#FFFFFF  align=center valign=center>"; 
	str=str+"<tr height=15>";
	str=str+"<td  bgcolor=#2379A5><b><font color=#FFFFFF size=+1 face=Verdana,Arial>Information</font></b></td>";
	str=str+"</tr>";
	str=str+"<tr><td align=center><font color=red size=2 face=Verdana,Arial>No Records Found</font></td></tr>";
	str=str+"</table>";
	str=str+"<br><br><br>";
	return str;
}

public String[] convertStringToArray(String str)
{
	String[] strarr=null;
	
	if(str!=null)
	{
		str=str.trim();	
		
		if(!str.equals(""))
		{
			strarr=new String[]{str};

		}
	}
	return strarr;
}

public String convertStringtoDate(String str)
{
	String	tempdt="";
	
	java.util.GregorianCalendar cal=new java.util.GregorianCalendar();
	try
	{
		cal.setTime(new java.util.Date(Long.parseLong(str+"000")));		
		tempdt=""+ Integer.toString(cal.get(Calendar.MONTH)+1) +"/"+cal.get(Calendar.DATE)+"/" +cal.get(Calendar.YEAR);
	}
	catch(NumberFormatException nfe)	
	{
		tempdt="-";
	}
	return tempdt;
}

public double convertCentsToDollars(String str)
{
	double amount=0;
	double intamount=0;
	
	
	try
	{
		amount=Integer.parseInt(str);
		amount=amount/100;
	}
	catch(NumberFormatException nfe)
	{
	
	}
	
	
	return amount;
}

public String NewShowConfirmation1(String msg,String error)
{
	String customerror=error;
	String soapex="SOAP-ENV";
	int pos=0;

	if(error.indexOf("Connection refused")!=-1 || error.indexOf("Error opening socket")!=-1)
	{
		msg="Server Down";
		customerror="Server is Down!";
	}
	else if(error.indexOf(soapex)!=-1 )
	{
		//	pos=error.indexOf(soapex);
		//	if(pos!=-1)
		//	customerror=error.substring(pos+soapex.length()+1);
	}
	
	
	String str="";
	str=str+"<br><br><br><center>";
	str=str+"<table border=1  cellpadding=2 cellspacing=0 height=30% width=50% bordercolorlight=#000000 bordercolordark=#FFFFFF  align=center valign=center>"; 
	str=str+"<tr height=15>";
	str=str+"<td  bgcolor=\"#2379A5\" colspan=\"3\"><font color=\"#FFFFFF\" size=\"1\" face=\"Verdana, Arial\"><b>"+msg+"</b></font></td>";
	str=str+"</tr>";
	str=str+"<tr><td align=center><font color=red size=2 face=Verdana,Arial>&nbsp;"+ customerror +"</font></td></tr>";
	str=str+"</table><center>";
	str=str+"<br><br><br></body></html>";
	return str;
}


public String ShowConfirmation(String msg,String stat)
{
	String str="";
	
	str=str+"<table width=\"400\" align=center border=1 cellpadding=2 cellspacing=0  bordercolorlight=#000000 bordercolordark=#FFFFFF>";
	str=str+"<tr>";
	str=str+"<td  bgcolor=\"#2379A5\" colspan=\"3\"><font color=\"#FFFFFF\" size=\"1\" face=\"Verdana, Arial\"><b>"+msg+"</b></font></td>";
	str=str+"</tr>";
	str=str+"<tr>";
	str=str+ "<td align=center>";
	
	str=str+"<br><br><font face=\"verdana,arial\" size=\"1\">"+ stat +"</font><br><br><br>";
	str=str+"</td></tr>";
	str=str+"</table>";
	return str;
}

public String dayoptions(int start,int end,String value)
{
	String str="";
	value=value.trim();
	
	String dayselected[]=new String[32];
	int j=Integer.parseInt(value);
	dayselected[j]="selected";
	
	for(int i=start;i<=end;i++)
	{
		str=str+"<option value=" + i +" "+dayselected[i]+">"+i+"</option>";
	}
	return str;	
}

public String yearoptions(int start,int end,String value)
{
	String str="";
	value=value.trim();
	
	String yearselected[]=new String[2037];
	int j=Integer.parseInt(value);
	yearselected[j]="selected";
	
	for(int i=start;i<=end;i++)
	{
		str=str+"<option value=" + i +" "+yearselected[i]+">"+i+"</option>";
	}
	return str;	
}

public String newmonthoptions(int start,int end,String value)
{
	String str="";
	value=value.trim();
	
	String month[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	
	String monthselected[]=new String[12];
	int j=Integer.parseInt(value);
	j=j-1;
	for(int k=0;k<=end;k++)
	{
		if(k==j)
		monthselected[k]="selected";
		else
		monthselected[k]="";
	}	
	
	for(int i=start;i<=end;i++)
	{
		str=str+"<option value=" + i +" "+monthselected[i]+">"+month[i] +"</option>";
	}
	return str;
}

public String printDOB(String str)
{
	String newstr=null;
	if(str!=null && !str.equals("0"))
	{
		newstr="";
		java.util.GregorianCalendar cal=new java.util.GregorianCalendar();
		cal.setTime(new java.util.Date(Long.parseLong(str+"000")));	
		newstr=newstr + "<select name=dd><option>dd</option>"+dayoptions(1,31,Integer.toString(cal.get(Calendar.DATE)))+ "</select>" + "<select  name=mm><option>mm</option>"+ newmonthoptions(0,11,Integer.toString(cal.get(Calendar.MONTH)+1))+ "</select>" + "<select name=yy><option>yyyy</option>"+ yearoptions(1920,2001,Integer.toString(cal.get(Calendar.YEAR)))+ "</select>" ;
	}	
	else
	{
		newstr="";
		newstr=newstr + "<select name=dd><option>dd</option>"+printoptions(1,31)+ "</select>" + "<select  name=mm><option>mm</option>"+ monthoptions(0,11)+ "</select>" + "<select name=yy><option>yyyy</option>"+ printoptions(1920,2001)+ "</select>" ;
	}
	return newstr;
}

public String printcontactdetails(Hashtable orderdata,String contactid)
{
	String str="";
	
	int size=Integer.parseInt((String)orderdata.get("numrows"));
	if(size>0)
	{
		Hashtable temphash=null;
		for(int pos=1;pos<=size;pos++) 
		{
			String id=Integer.toString(pos);
			temphash=(Hashtable)orderdata.get(id);
			str=str+"<option value="+ temphash.get("contactid") +">"+temphash.get("name")+"</option>";
		}
	
	}
	return str;
}

public String printInfocontactdetails(Hashtable orderdata)
{
	String str="";
	
	int size=Integer.parseInt((String)orderdata.get("numrows"));
	if(size>0)
	{
		Hashtable temphash=null;
		
		for(int pos=1;pos<=size;pos++) 
		{
			String id=Integer.toString(pos);
			temphash=(Hashtable)orderdata.get(id);
			if(temphash.get("roid")!=null)
			str=str+"<option value="+ temphash.get("contactid") +">"+temphash.get("name")+ " - " +temphash.get("roid")+"</option>";
		}
	
	}
	return str;
}


public String printAppServer(Hashtable orderdata,String value,String text)
{
	String str="";
	
	int size=Integer.parseInt((String)orderdata.get("numrows"));
	
	if(size>0)
	{
		Hashtable temphash=null;
		for(int pos=1;pos<=size;pos++) 
		{
			String id=Integer.toString(pos);
			temphash=(Hashtable)orderdata.get(id);
			str=str+"<option value="+ temphash.get(value) +">"+temphash.get(text)+"</option>";
		}
	
	}
	return str;
}

public String ShowLandRush(String msg,String stat)
{
	String str="";
	
	str=str+"<table width=\"75%\" align=center border=1 cellpadding=2 cellspacing=0  bordercolorlight=#000000 bordercolordark=#FFFFFF>";
	str=str+"<tr>";
	str=str+"<td  bgcolor=\"#2379A5\" colspan=\"3\"><font color=\"#FFFFFF\" size=\"1\" face=\"Verdana, Arial\"><b>"+msg+"</b></font></td>";
	str=str+"</tr>";
	str=str+"<tr>";
	str=str+ "<td>";
	
	str=str+"<br><br><font face=\"verdana,arial\" size=\"2\">"+ stat +"</font><br><br><br>";
	str=str+"</td></tr>";
	str=str+"</table>";
	return str;
}

public String countryoptions(String name,String defaultvalue,String defaulttext,String selectedcountry)
{
	selectedcountry=checkStringNull(selectedcountry);

	StringBuffer str= new StringBuffer();
	str.append("<SELECT name=" + name +">");
	
	if(defaultvalue!=null && defaulttext!=null)
	str.append("<OPTION value=\""+ defaultvalue +"\">" + defaulttext);
	
	String country[]={"<OPTION value=AF>Afghanistan","<OPTION value=AL>Albania","<OPTION value=DZ>Algeria","<OPTION value=AS>American Samoa","<OPTION value=AD>Andorra","<OPTION value=AO>Angola","<OPTION value=AI>Anguilla","<OPTION value=AQ>Antarctica","<OPTION value=AG>Antigua And Barbuda","<OPTION value=AR>Argentina<OPTION value=AM>Armenia","","<OPTION value=AW>Aruba","<OPTION value=AU>Australia","<OPTION value=AT>Austria","<OPTION value=AZ>Azerbaijan","<OPTION value=BS>Bahamas, The","<OPTION value=BH>Bahrain","<OPTION value=BD>Bangladesh","<OPTION value=BB>Barbados","<OPTION value=BY>Belarus","<OPTION value=BE>Belgium"  ,"<OPTION value=BZ>Belize","<OPTION value=BJ>Benin","<OPTION value=BM>Bermuda","<OPTION value=BT>Bhutan","<OPTION value=BO>Bolivia","<OPTION value=BA>Bosnia and Herzegovina","<OPTION value=BW>Botswana","<OPTION value=BV>Bouvet Island","<OPTION value=BR>Brazil","<OPTION value=IO>British Indian Ocean Territory","<OPTION value=BN>Brunei","<OPTION value=BG>Bulgaria","<OPTION value=BF>Burkina Faso","<OPTION value=BI>Burundi","<OPTION value=KH>Cambodia","<OPTION value=CM>Cameroon","<OPTION value=CA>Canada","<OPTION value=CV>Cape Verde","<OPTION value=KY>Cayman Islands","<OPTION value=CF>Central African Republic","<OPTION value=TD>Chad","<OPTION value=CL>Chile","<OPTION value=CN>China","<OPTION value=CX>Christmas Island","<OPTION value=CC>Cocos (Keeling) Islands","<OPTION value=CO>Colombia","<OPTION value=KM>Comoros","<OPTION value=CG>Congo","<OPTION value=CD>Congo, Democractic Republic of the","<OPTION value=CK>Cook Islands","<OPTION value=CR>Costa Rica","<OPTION value=CI>Cote D'Ivoire (Ivory Coast)","<OPTION value=HR>Croatia (Hrvatska)","<OPTION value=CU>Cuba","<OPTION value=CY>Cyprus","<OPTION value=CZ>Czech Republic","<OPTION value=DK>Denmark","<OPTION value=DJ>Djibouti","<OPTION value=DM>Dominica","<OPTION value=DO>Dominican Republic","<OPTION value=TP>East Timor","<OPTION value=EC>Ecuador","<OPTION value=EG>Egypt","<OPTION value=SV>El Salvador","<OPTION value=GQ>Equatorial Guinea","<OPTION value=ER>Eritrea","<OPTION value=EE>Estonia","<OPTION value=ET>Ethiopia","<OPTION value=FK>Falkland Islands (Islas Malvinas)","<OPTION value=FO>Faroe Islands","<OPTION value=FJ>Fiji Islands","<OPTION value=FI>Finland","<OPTION value=FR>France","<OPTION value=GF>French Guiana","<OPTION value=PF>French Polynesia","<OPTION value=TF>French Southern Territories","<OPTION value=GA>Gabon","<OPTION value=GM>Gambia, The","<OPTION value=GE>Georgia","<OPTION value=DE>Germany","<OPTION value=GH>Ghana","<OPTION value=GI>Gibraltar","<OPTION value=GR>Greece","<OPTION value=GL>Greenland","<OPTION value=GD>Grenada","<OPTION value=GP>Guadeloupe","<OPTION value=GU>Guam","<OPTION value=GT>Guatemala","<OPTION value=GN>Guinea","<OPTION value=GW>Guinea-Bissau","<OPTION value=GY>Guyana","<OPTION value=HT>Haiti","<OPTION value=HM>Heard and McDonald Islands","<OPTION value=HN>Honduras","<OPTION value=HK>Hong Kong S.A.R.","<OPTION value=HU>Hungary","<OPTION value=IS>Iceland","<OPTION value=IN>India","<OPTION value=ID>Indonesia","<OPTION value=IR>Iran","<OPTION value=IQ>Iraq","<OPTION value=IE>Ireland","<OPTION value=IL>Israel","<OPTION value=IT>Italy","<OPTION value=JM>Jamaica","<OPTION value=JP>Japan","<OPTION value=JO>Jordan","<OPTION value=KZ>Kazakhstan","<OPTION value=KE>Kenya","<OPTION value=KI>Kiribati","<OPTION value=KR>Korea","<OPTION value=KP>Korea, North","<OPTION value=KW>Kuwait","<OPTION value=KG>Kyrgyzstan","<OPTION value=LA>Laos","<OPTION value=LV>Latvia","<OPTION value=LB>Lebanon","<OPTION value=LS>Lesotho","<OPTION value=LR>Liberia","<OPTION value=LY>Libya","<OPTION value=LI>Liechtenstein","<OPTION value=LT>Lithuania","<OPTION value=LU>Luxembourg","<OPTION value=MO>Macau S.A.R.","<OPTION value=MK>Macedonia, Former Yugoslav Republic of","<OPTION value=MG>Madagascar","<OPTION value=MW>Malawi","<OPTION value=MY>Malaysia","<OPTION value=MV>Maldives","<OPTION value=ML>Mali","<OPTION value=MT>Malta","<OPTION value=MH>Marshall Islands","<OPTION value=MQ>Martinique","<OPTION value=MR>Mauritania","<OPTION value=MU>Mauritius","<OPTION value=YT>Mayotte","<OPTION value=MX>Mexico","<OPTION value=FM>Micronesia","<OPTION value=MD>Moldova","<OPTION value=MC>Monaco","<OPTION value=MN>Mongolia","<OPTION value=MS>Montserrat","<OPTION value=MA>Morocco","<OPTION value=MZ>Mozambique","<OPTION value=MM>Myanmar","<OPTION value=NA>Namibia","<OPTION value=NR>Nauru","<OPTION value=NP>Nepal","<OPTION value=AN>Netherlands Antilles","<OPTION value=NL>Netherlands, The","<OPTION value=NC>New Caledonia","<OPTION value=NZ>New Zealand","<OPTION value=NI>Nicaragua","<OPTION value=NE>Niger","<OPTION value=NG>Nigeria","<OPTION value=NU>Niue","<OPTION value=NF>Norfolk Island","<OPTION value=MP>Northern Mariana Islands","<OPTION value=NO>Norway","<OPTION value=OM>Oman","<OPTION value=PK>Pakistan","<OPTION value=PW>Palau","<OPTION value=PA>Panama","<OPTION value=PG>Papua new Guinea","<OPTION value=PY>Paraguay","<OPTION value=PE>Peru","<OPTION value=PH>Philippines","<OPTION value=PN>Pitcairn Island","<OPTION value=PL>Poland","<OPTION value=PT>Portugal","<OPTION value=PR>Puerto Rico","<OPTION value=QA>Qatar","<OPTION value=RE>Reunion","<OPTION value=RO>Romania","<OPTION value=RU>Russia","<OPTION value=RW>Rwanda","<OPTION value=SH>Saint Helena","<OPTION value=KN>Saint Kitts And Nevis","<OPTION value=LC>Saint Lucia","<OPTION value=PM>Saint Pierre and Miquelon","<OPTION value=VC>Saint Vincent And The Grenadines","<OPTION value=WS>Samoa","<OPTION value=SM>San Marino","<OPTION value=ST>Sao Tome and Principe","<OPTION value=SA>Saudi Arabia","<OPTION value=SN>Senegal","<OPTION value=SC>Seychelles","<OPTION value=SL>Sierra Leone","<OPTION value=SG>Singapore","<OPTION value=SK>Slovakia","<OPTION value=SI>Slovenia","<OPTION value=SB>Solomon Islands","<OPTION value=SO>Somalia","<OPTION value=ZA>South Africa","<OPTION value=GS>South Georgia And The South Sandwich Islands","<OPTION value=ES>Spain","<OPTION value=LK>Sri Lanka","<OPTION value=SD>Sudan","<OPTION value=SR>Suriname","<OPTION value=SJ>Svalbard And Jan Mayen Islands","<OPTION value=SZ>Swaziland","<OPTION value=SE>Sweden","<OPTION value=CH>Switzerland","<OPTION value=SY>Syria","<OPTION value=TW>Taiwan","<OPTION value=TJ>Tajikistan","<OPTION value=TZ>Tanzania","<OPTION value=TH>Thailand","<OPTION value=TG>Togo","<OPTION value=TK>Tokelau","<OPTION value=TO>Tonga","<OPTION value=TT>Trinidad And Tobago","<OPTION value=TN>Tunisia","<OPTION value=TR>Turkey","<OPTION value=TM>Turkmenistan","<OPTION value=TC>Turks And Caicos Islands","<OPTION value=TV>Tuvalu","<OPTION value=UG>Uganda","<OPTION value=UA>Ukraine","<OPTION value=AE>United Arab Emirates","<OPTION value=UK>United Kingdom","<OPTION value=US>United States","<OPTION value=UM>United States Minor Outlying Islands","<OPTION value=UY>Uruguay","<OPTION value=UZ>Uzbekistan","<OPTION value=VU>Vanuatu","<OPTION value=VA>Vatican City State (Holy See)","<OPTION value=VE>Venezuela","<OPTION value=VN>Vietnam","<OPTION value=VG>Virgin Islands (British)","<OPTION value=VI>Virgin Islands (US)","<OPTION value=WF>Wallis And Futuna Islands","<OPTION value=YE>Yemen","<OPTION value=YU>Yugoslavia","<OPTION value=ZM>Zambia","<OPTION value=ZW>Zimbabwe</OPTION>"};
	for(int i=0 ; i < country.length ; i++)
	{
		if(selectedcountry!=null)
		{
			if(country[i].indexOf(selectedcountry+">") > 0)
			{
					StringBuffer sb = new StringBuffer(country[i]);
					sb.insert(country[i].indexOf(">")," selected");

					str.append(sb.toString());
			}
			else
			str.append(country[i]);
	    }
		else
			str.append(country[i]);
	}
	str.append("</SELECT>");
return str.toString();
}
%>
