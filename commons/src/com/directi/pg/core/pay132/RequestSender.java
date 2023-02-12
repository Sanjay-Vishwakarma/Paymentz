package com.directi.pg.core.pay132;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 13, 2012
 * Time: 9:01:01 AM
 * To change this template use File | Settings | File Templates.
 */


import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.log4j.Logger;
import com.directi.pg.SystemError;


public class RequestSender {
	private String serverUrl = null;

	private Logger log = Logger.getLogger(this.getClass());

	public RequestSender(String url) {
		this.serverUrl = url;
	}

	public String send(String xml) throws PZTechnicalViolationException
    {
		com.directi.pg.core.pay132.UrlRequest req = new UrlRequest(UrlRequest.POST, serverUrl);

		

        ByteBuffer buffer =null;

			//send it
			req.addParam("post_data", xml);
			buffer = req.send();



		return buffer.toString();
	}
}
