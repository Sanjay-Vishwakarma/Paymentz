package com.payment.icici.com.sfa.java;
public class SessionDetail
{
	private String mstrTransactionIPAddr=null;

	private String mstrBrowserCountry=null;
	private String mstrBrowserLocalLang=null;
	private String mstrBrowserLocalLangVariant =null;
	private String mstrBrowserUserAgent  = null;
	private String mstrSecureCookie= null;
	private String sessionDetailIsAvailable = null;


	public void setSessionDetails(String astrTransactionIPAddr,String astrSecureCookie,String astrBrowserCountry,String astrBrowserLocalLang,String astrBrowserLocalLangVariant
			, String astrBrowserUserAgent)
	{
		this.mstrBrowserCountry			=	astrBrowserCountry;
		this.mstrBrowserLocalLang			=	astrBrowserLocalLang;
		this.mstrBrowserLocalLangVariant	=	astrBrowserLocalLangVariant;
		this.mstrBrowserUserAgent			=	astrBrowserUserAgent;
		this.mstrSecureCookie				=	astrSecureCookie;
		this.mstrTransactionIPAddr			=	astrTransactionIPAddr;
		this.sessionDetailIsAvailable		=	"YES";

	}
	public String getSessionDetailFlag()
	{
		return this.sessionDetailIsAvailable ;
	}
	public String getTransactionIPAddr()
	{
		return this.mstrTransactionIPAddr;
	}

	public String getBrowserCountry()
	{
		return this.mstrBrowserCountry;
	}
	public String getBrowserLocalLang()
	{
		return this.mstrBrowserLocalLang;
	}
	public String getBrowserLocalLangVariant()
	{
		return this.mstrBrowserLocalLangVariant;
	}
	public String getBrowserUserAgent()
	{
		return this.mstrBrowserUserAgent;
	}
	public String getSecureCookie()
	{
		return this.mstrSecureCookie;
	}
}
