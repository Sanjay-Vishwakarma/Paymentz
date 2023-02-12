package com.directi.pg.core.mymonedero;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

public class SSLConnectionUtils
{

	//private static final String upopHost = "test.ctpe.net";
    //private static final String upopHost = "demomonedero.wavecrest.gi";
   // private static final String upopHost = "mymonedero.com";
   //private static final String upopHost = "integration.processing.com";
	private static final String upopHost = "staging.pz.com";


	private static final int HTTPS_PORT = 443;

	// certificate file alias to import
	//private static final String certFileAlias = "paylineVoucher";

    //private static final String certFileAlias = "MyMonedero";
    private static final String certFileAlias = "StagingPz";

	// Java Trust Store password, initial password is "changeit"
	private static final String trustStorePassword = "changeit";

	// Java Trust Store file path
	//private static final String JAVA_HOME = "C:\\Program Files\\Java\\jdk1.7.0_10\\jre";
   // private static final String JAVA_HOME = "C:\\Program Files (x86)\\Java\\jdk1.7.0_04\\jre";
	private static final String JAVA_HOME_SYS = System.getProperty("java.home");
	private static final String trustStoreFileRelativePath = "/lib/security/cacerts";

	/**
	 * import certificate to java key store
	 *
	 * @param jksPath
	 *            Trust Store file path
	 * @param jksPassword
	 *            Trust Store password
	 */
	public static void importCertificate(String jksPath, String jksPassword) {
		try {
			Certificate certificate = getUpopCertificate();
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(null, jksPassword.toCharArray());
			setRemainCertificates(ks, jksPath, jksPassword);
			ks.setCertificateEntry(certFileAlias, certificate);
			ks.store(new FileOutputStream(jksPath), jksPassword.toCharArray());
			//System.out.println("Import Certificate " + certFileAlias + " Successful!");
		} catch (Exception e) {
			throw new RuntimeException("Exception while importing certificate to Java Key Store for unionpaysecure certificate.",
					e);
		}
	}

	/**
	 * import certificate to java key store
	 *
	 */
	public static void importCertificate() {
		try {
			String jksPath = JAVA_HOME_SYS + trustStoreFileRelativePath;
			System.setProperty("https.protocols", "TLSv1.2");
			System.setProperty("jsse.enableSNIExtension", "false");
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(null, trustStorePassword.toCharArray());
			setRemainCertificates(ks, jksPath, trustStorePassword);
			Certificate certificate = getUpopCertificate();
			ks.setCertificateEntry(certFileAlias, certificate);
			ks.store(new FileOutputStream(jksPath), trustStorePassword.toCharArray());
			//System.out.println("Import Certificate " + certFileAlias + " Successful!");
		} catch (Exception e) {
			throw new RuntimeException("Exception while importing certificate to Java Key Store for unionpaysecure certificate.",
					e);
		}
	}

	/**
	 * keep the original certificate in java key store
	 *
	 * @param ks
	 * @param jksPath
	 * @param jksPassword
	 */
	private static void setRemainCertificates(KeyStore ks, String jksPath, String jksPassword) {
		try {
			FileInputStream in = new FileInputStream(jksPath);
			KeyStore ks4RemainCerts = KeyStore.getInstance("JKS");
			Certificate c = null;
			ks4RemainCerts.load(in, jksPassword.toCharArray());
			Enumeration<String> e = ks4RemainCerts.aliases();
			String alias;
			while (e.hasMoreElements()) {
				alias = (String) e.nextElement();
				c = ks4RemainCerts.getCertificate(alias);
				ks.setCertificateEntry(alias, c);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Exception while setting remain certificates to Java Key Store for unionpaysecure certificate.", e);
		}
	}

	/**
	 * delete one certificate
	 *
	 * @param alias
	 * @param jksPath
	 * @param jksPassword
	 */
	public static void deleteCertificate(String alias, String jksPath, String jksPassword) {
		try {
			FileInputStream in = new FileInputStream(jksPath);
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(in, jksPassword.toCharArray());
			if (ks.containsAlias(alias)) {
				ks.deleteEntry(alias);
				ks.store(new FileOutputStream(jksPath), jksPassword.toCharArray());
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception while deleting certificate from Java Key Store.", e);
		}
	}

	/**
	 * delete one certificate
	 *
	 * @param alias
	 */
	public static void deleteCertificate(String alias) {
		try {
			String jksPath = JAVA_HOME_SYS + trustStoreFileRelativePath;
			FileInputStream in = new FileInputStream(jksPath);
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(in, trustStorePassword.toCharArray());
			if (ks.containsAlias(alias)) {
				ks.deleteEntry(alias);
				ks.store(new FileOutputStream(jksPath), trustStorePassword.toCharArray());
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception while deleting certificate from Java Key Store.", e);
		}
	}

	/**
	 * print certificate information
	 *
	 * @param jksPath
	 * @param jksPassword
	 */
	public static void printCertificateInfo(String jksPath, String jksPassword) {
		try {
			FileInputStream in = new FileInputStream(jksPath);
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(in, jksPassword.toCharArray());
			Enumeration<String> e = ks.aliases();
			String alias;
			while (e.hasMoreElements()) {
				alias = (String) e.nextElement();
				if (certFileAlias.equals(alias)) {
					//System.out.println("Your imported certificate " + alias);
					Certificate c = ks.getCertificate(alias);
					//System.out.println("Content: " + c.toString());
				}

			}
		} catch (Exception e) {
			throw new RuntimeException("Exception while printing certificates in Java Key Store.", e);
		}
	}

	/**
	 * print certificate information
	 */
	public static void printCertificateInfo() {
		try {
			String jksPath = JAVA_HOME_SYS + trustStoreFileRelativePath;
			FileInputStream in = new FileInputStream(jksPath);
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(in, trustStorePassword.toCharArray());
			Enumeration<String> e = ks.aliases();
			String alias;
			while (e.hasMoreElements()) {
				alias = (String) e.nextElement();
				if (certFileAlias.equals(alias)) {
					//System.out.println("Your imported certificate " + alias);
					Certificate c = ks.getCertificate(alias);
					//System.out.println("Content: " + c.toString());
				}

			}
		} catch (Exception e) {
			throw new RuntimeException("Exception while printing certificates in Java Key Store.", e);
		}
	}

	/**
	 * get certificate from UPOP website
	 *
	 * @return
	 */
	public static Certificate getUpopCertificate() {

		try {
			TrustManager trm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				//@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
				}

				//@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
				}
			};

			//SSLContext sc = SSLContext.getInstance("SSL");
			SSLContext sc = SSLContext.getInstance("TLSv1.2");
			sc.init(null, new TrustManager[] { trm }, null);
			SSLSocketFactory factory = sc.getSocketFactory();
			SSLSocket socket = (SSLSocket) factory.createSocket(upopHost, HTTPS_PORT);

			socket.startHandshake();
			SSLSession session = socket.getSession();
			Certificate[] servercerts = session.getPeerCertificates();
			socket.close();
			return servercerts[0];
		} catch (Exception e) {
			throw new RuntimeException("Exception while get upop certificate.", e);
		}
	}

	public static void main(String[] args) throws Exception {
//		String jksFilePath = JAVA_HOME + trustStoreFileRelativePath;
//		deleteCertificate(certFileAlias, jksFilePath, trustStorePassword);
//		printCertificateInfo(jksFilePath, trustStorePassword);
//		importCertificate(jksFilePath, trustStorePassword);
//		printCertificateInfo(jksFilePath, trustStorePassword);

		deleteCertificate(certFileAlias);
		importCertificate();
		printCertificateInfo();
	}
}
