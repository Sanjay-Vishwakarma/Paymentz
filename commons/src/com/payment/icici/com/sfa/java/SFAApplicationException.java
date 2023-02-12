package com.payment.icici.com.sfa.java;

import com.directi.pg.TransactionLogger;

import java.io.* ;

public class SFAApplicationException extends Exception  {

	private Throwable monestedException ;
	private String    mstrerrorCode ;
	private static TransactionLogger transactionLogger = new TransactionLogger(SFAApplicationException.class.getName());

	public  SFAApplicationException (String astrmessage) {
		this(null,astrmessage,null);
	}

	public  SFAApplicationException (String astrerrorCode, String astrmessage) {
		this(astrerrorCode,astrmessage,null);
	}

	public  SFAApplicationException (String astrerrorCode, String astrmessage,
									 Throwable aoexception) {
		super(astrmessage);
		this.mstrerrorCode = astrerrorCode ;
		this.monestedException = aoexception ;
	}

	public String getErrorCode () {
		return mstrerrorCode ;
	}

	public Throwable getNestedException () {
		return monestedException ;
	}

	public String toString () {
		return getClass().getName() + ": "
				+ ((mstrerrorCode == null) ? "" : (mstrerrorCode + ": "))
				+ getMessage() ;
	}

	public void printStackTrace () {
		if ( monestedException != null ) {
			//monestedException.printStackTrace();
			transactionLogger.error("printStackTrace 1 ::::::",monestedException);
		}
	}

	public void printStackTrace (PrintWriter aowriter ) {
		if ( monestedException != null ) {
			//monestedException.printStackTrace(aowriter);
			transactionLogger.error("printStackTrace 2 ::::::",monestedException);
		}
	}

	public void printStackTrace (PrintStream aostream) {
		if ( monestedException != null ) {
		//	monestedException.printStackTrace(aostream);
			transactionLogger.error("printStackTrace 3 ::::::",monestedException);
		}
	}



}