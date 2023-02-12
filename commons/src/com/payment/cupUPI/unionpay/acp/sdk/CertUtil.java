/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 *
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 *
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28       Certificate tools
 * =============================================================================
 */
package com.payment.cupUPI.unionpay.acp.sdk;

import com.directi.pg.UnionPayInternationalLogger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPublicKeySpec;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.payment.cupUPI.unionpay.acp.sdk.SDKConstants.UNIONPAY_CNNAME;
import static com.payment.cupUPI.unionpay.acp.sdk.SDKUtil.isEmpty;
/**
 * @ClassName: CertUtil
 * @Description: acpsdk certificate tools are mainly used to load and use certificates.
 * @date 2016-7-22 ??2:46:20
 *
 */
public class CertUtil {

    private static UnionPayInternationalLogger transactionLogger = new UnionPayInternationalLogger(CertUtil.class.getName());

    /**  */
    private static KeyStore keyStore = null;
    /** Encryption public key and certificate for sensitive information */
    private static X509Certificate encryptCert = null;
    /** Encryption public key for magnetic tracks */
    private static PublicKey encryptTrackKey = null;
    /** Verify the messages, signatures, and certificates returned from China UnionPay. */
    private static X509Certificate validateCert = null;
    /** Authenticate the signatures of intermediate certificates */
    private static X509Certificate middleCert = null;
    /** Authenticate the signatures of root certificates */
    private static X509Certificate rootCert = null;
    /** The map used to store the public key certificates used to verify the messages and signatures returned from China UnionPay. */
    private static Map<String, X509Certificate> certMap = new HashMap<String, X509Certificate>();
    /**  */
    private final static Map<String, KeyStore> keyStoreMap = new ConcurrentHashMap<String, KeyStore>();

    static {
        init();
    }

    /**
     * Initialize all certificates.
     */
    private static void init() {
        try {
            addProvider();//Add BC provider to the system
            initSignCert();//Initialize signed private key certificates
            initMiddleCert();//Initialize the intermediate certificates among the certificates for verifying certificates
            initRootCert();//Initialize the root certificates among the certificates for verifying certificates
            initEncryptCert();//Initialize encrypted public keys
            initTrackKey();//Construct encryption public key for magnetic tracks
            initValidateCertFromDir();//Initialize all certificates  for verifying certificates
        } catch (Exception e) {
            transactionLogger.error("Initialization fails. (This can be regarded as an exception if a symmetric key is used for signature.)" + e);
        }
    }

    /**
     * Add signature, signature authentication, and encryption algorithm providers
     */
    private static void addProvider(){
        if (Security.getProvider("BC") == null) {
            transactionLogger.error("add BC provider");
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        } else {
            Security.removeProvider("BC"); //When tomcat is automatically re-loaded during eclipse debugging, an exception occurs to BC for unknown reason.
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            transactionLogger.error("re-add BC provider");
        }
        printSysInfo();
    }

    /**
     * Load signed certificates by using the private key paths and passwords configured in the configuration file acp_sdk.properties
     */
    private static void initSignCert() {

        transactionLogger.error("SDKConfig.getConfig().getSignMethod()---" + SDKConfig.getConfig().getSignMethod());
        if(!"01".equals(SDKConfig.getConfig().getSignMethod())){
            transactionLogger.error("No signed certificate will be loaded if the certificates are not signed based on RSA.");
            return;
        }
        if (SDKConfig.getConfig().getSignCertPath() == null
                || SDKConfig.getConfig().getSignCertPwd() == null
                || SDKConfig.getConfig().getSignCertType() == null) {
            transactionLogger.error("WARN: " + SDKConfig.SDK_SIGNCERT_PATH + "Or" + SDKConfig.SDK_SIGNCERT_PWD
                    + "Or" + SDKConfig.SDK_SIGNCERT_TYPE + "Null.  Stop loading signed certificates.");
            return;
        }
        if (null != keyStore) {
            keyStore = null;
        }
        try {
            keyStore = getKeyInfo(SDKConfig.getConfig().getSignCertPath(),
                    SDKConfig.getConfig().getSignCertPwd(), SDKConfig.getConfig().getSignCertType());
            transactionLogger.error("InitSignCert Successful. CertId=[" + getSignCertId() + "]");
        } catch (IOException e) {
            transactionLogger.error("InitSignCert Error" + e);
        }
    }

    /**
     * Load the encryption certificates for sensitive information by using the paths configured in the configuration file acp_sdk.properties
     */
    private static void initMiddleCert() {
        transactionLogger.error("Load intermediate certificates==>" + SDKConfig.getConfig().getMiddleCertPath());
        if (!isEmpty(SDKConfig.getConfig().getMiddleCertPath())) {
            middleCert = initCert(SDKConfig.getConfig().getMiddleCertPath());
            transactionLogger.error("Load MiddleCert Successful");
        } else {
            transactionLogger.error("WARN: acpsdk.middle.path is empty");
        }
    }

    /**
     * Load the encryption certificates for sensitive information by using the paths configured in the configuration file acp_sdk.properties
     */
    private static void initRootCert() {
        transactionLogger.error("Load root certificates==>" + SDKConfig.getConfig().getRootCertPath());
        if (!isEmpty(SDKConfig.getConfig().getRootCertPath())) {
            rootCert = initCert(SDKConfig.getConfig().getRootCertPath());
            transactionLogger.error("Load RootCert Successful");
        } else {
            transactionLogger.error("WARN: acpsdk.rootCert.path is empty");
        }
    }

    /**
     * Load the public key superior certificates of China UnionPay by using the paths configured in the configuration file acp_sdk.properties
     */
    private static void initEncryptCert() {
        transactionLogger.error("Load the encryption certificates for sensitive information==>" + SDKConfig.getConfig().getEncryptCertPath());
        if (!isEmpty(SDKConfig.getConfig().getEncryptCertPath())) {
            encryptCert = initCert(SDKConfig.getConfig().getEncryptCertPath());
            transactionLogger.error("Load EncryptCert Successful");
        } else {
            transactionLogger.error("WARN: acpsdk.encryptCert.path is empty");
        }
    }

    /**
     * Load the public keys for magnetic tracks by using the paths configured in the configuration file acp_sdk.properties
     */
    private static void initTrackKey() {
        if (!isEmpty(SDKConfig.getConfig().getEncryptTrackKeyModulus())
                && !isEmpty(SDKConfig.getConfig().getEncryptTrackKeyExponent())) {
            encryptTrackKey = getPublicKey(SDKConfig.getConfig().getEncryptTrackKeyModulus(),
                    SDKConfig.getConfig().getEncryptTrackKeyExponent());
            transactionLogger.error("LoadEncryptTrackKey Successful");
        } else {
            transactionLogger.error("WARN: acpsdk.encryptTrackKey.modulus or acpsdk.encryptTrackKey.exponent is empty");
        }
    }

    /**
     * Load the certificates for which signature authentication has been conducted by using the paths configured in the configuration file acp_sdk.properties
     */
    private static void initValidateCertFromDir() {
        if(!"01".equals(SDKConfig.getConfig().getSignMethod())){
            transactionLogger.error("No certificate  for verifying certificates will be loaded if the certificates are not signed based on RSA.");
            return;
        }
        certMap.clear();
        String dir = SDKConfig.getConfig().getValidateCertDir();
        transactionLogger.error("Load the catalog of certificates for which signature authentication has been conducted==>" + dir + " Note: If the version is 5.1.0 in the request message, the catalog of the certificates for verifying certificates will not be used and does not need to be set (this catalog, however, must be set if the version is 5.0.0).");
        if (isEmpty(dir)) {
            transactionLogger.error("WARN: acpsdk.validateCert.dir is empty");
            return;
        }
        CertificateFactory cf = null;
        FileInputStream in = null;
        try {
            cf = CertificateFactory.getInstance("X.509", "BC");
            File fileDir = new File(dir);
            File[] files = fileDir.listFiles(new CerFilter());
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                in = new FileInputStream(file.getAbsolutePath());
                validateCert = (X509Certificate) cf.generateCertificate(in);
                certMap.put(validateCert.getSerialNumber().toString(),
                        validateCert);
                // Print the information about certificate loading for debugging at the test phase
                transactionLogger.error("[" + file.getAbsolutePath() + "][CertId="
                        + validateCert.getSerialNumber().toString() + "]");
            }
            transactionLogger.error("LoadVerifyCert Successful");
        } catch (CertificateException e) {
            transactionLogger.error("LoadVerifyCert Error" + e);
        } catch (FileNotFoundException e) {
            transactionLogger.error("LoadVerifyCert Error File Not Found" + e);
        } catch (NoSuchProviderException e) {
            transactionLogger.error("LoadVerifyCert Error No BC Provider" + e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    transactionLogger.error(e.toString());
                }
            }
        }
    }

    /**
     * Load the signed certificates by using the paths and passwords that have been bound, and save the certificates in certKeyStoreMap.
     *
     * @param certFilePath
     * @param certPwd
     */
    private static void loadSignCert(String certFilePath, String certPwd) {
        KeyStore keyStore = null;
        try {
            keyStore = getKeyInfo(certFilePath, certPwd, "PKCS12");
            keyStoreMap.put(certFilePath, keyStore);
            transactionLogger.error("LoadRsaCert Successful");
        } catch (IOException e) {
            transactionLogger.error("LoadRsaCert Error" + e);
        }
    }

    /**
     * Initialize these certificates to public key certificates by means of their paths.
     * @param path
     * @return
     */
    private static X509Certificate initCert(String path) {
        X509Certificate encryptCertTemp = null;
        CertificateFactory cf = null;
        FileInputStream in = null;
        try {
            cf = CertificateFactory.getInstance("X.509", "BC");
            in = new FileInputStream(path);
            encryptCertTemp = (X509Certificate) cf.generateCertificate(in);
            // Print the information about certificate loading for debugging at the test phase
            transactionLogger.error("[" + path + "][CertId="
                    + encryptCertTemp.getSerialNumber().toString() + "]");
        } catch (CertificateException e) {
            transactionLogger.error("InitCert Error" + e);
        } catch (FileNotFoundException e) {
            transactionLogger.error("InitCert Error File Not Found" + e);
        } catch (NoSuchProviderException e) {
            transactionLogger.error("LoadVerifyCert Error No BC Provider" + e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    transactionLogger.error(e.toString());
                }
            }
        }
        return encryptCertTemp;
    }

    /**
     * Obtain the PrivateKey objects of signed private key certificates by means of keyStore
     *
     * @return
     */
    public static PrivateKey getSignCertPrivateKey() {
        try {
            Enumeration<String> aliasenum = keyStore.aliases();
            String keyAlias = null;
            if (aliasenum.hasMoreElements()) {
                keyAlias = aliasenum.nextElement();
            }
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias,
                    SDKConfig.getConfig().getSignCertPwd().toCharArray());
            return privateKey;
        } catch (KeyStoreException e) {
            transactionLogger.error("getSignCertPrivateKey Error" + e);
            return null;
        } catch (UnrecoverableKeyException e) {
            transactionLogger.error("getSignCertPrivateKey Error" + e);
            return null;
        } catch (NoSuchAlgorithmException e) {
            transactionLogger.error("getSignCertPrivateKey Error" + e);
            return null;
        }
    }
    /**
     * Obtain PrivateKey objects by means of the private key certificates at the specified path
     *
     * @return
     */
    public static PrivateKey getSignCertPrivateKeyByStoreMap(String certPath,
                                                             String certPwd) {
        if (!keyStoreMap.containsKey(certPath)) {
            loadSignCert(certPath, certPwd);
        }
        try {
            Enumeration<String> aliasenum = keyStoreMap.get(certPath)
                    .aliases();
            String keyAlias = null;
            if (aliasenum.hasMoreElements()) {
                keyAlias = aliasenum.nextElement();
            }
            PrivateKey privateKey = (PrivateKey) keyStoreMap.get(certPath)
                    .getKey(keyAlias, certPwd.toCharArray());
            return privateKey;
        } catch (KeyStoreException e) {
            transactionLogger.error("getSignCertPrivateKeyByStoreMap Error" + e);
            return null;
        } catch (UnrecoverableKeyException e) {
            transactionLogger.error("getSignCertPrivateKeyByStoreMap Error" + e);
            return null;
        } catch (NoSuchAlgorithmException e) {
            transactionLogger.error("getSignCertPrivateKeyByStoreMap Error" + e);
            return null;
        }
    }

    /**
     * Obtain the PublicKey of the encryption certificate for sensitive information
     *
     * @return
     */
    public static PublicKey getEncryptCertPublicKey() {

        transactionLogger.error("encryptCert---");
        if (null == encryptCert)
        {
            String path = SDKConfig.getConfig().getEncryptCertPath();
            transactionLogger.error("getEncryptCertPublicKey Path---" + path);
            if (!isEmpty(path)) {
                encryptCert = initCert(path);
                return encryptCert.getPublicKey();
            } else {
                transactionLogger.error("acpsdk.encryptCert.path is empty");
                return null;
            }
        }
        else
        {
            //transactionLogger.error("encryptCert.getPublicKey()---"+encryptCert.getPublicKey());
            return encryptCert.getPublicKey();
        }
    }

    /**
     * Reset the public key of the encryption certificate for sensitive information
     */
    public static void resetEncryptCertPublicKey() {
        encryptCert = null;
    }

    /**
     * Obtain the PublicKey of the encryption certificate for magnetic tracks
     *
     * @return
     */
    public static PublicKey getEncryptTrackPublicKey() {
        if (null == encryptTrackKey) {
            initTrackKey();
        }
        return encryptTrackKey;
    }

    /**
     * Obtain the corresponding certificate PublicKey in the Map for certificates  for verifying certificates by means of certID
     *
     * //@param certID: Physical serial number of the certificate
     * @return Public keys obtained by means of certificate numbers
     */
    public static PublicKey getValidatePublicKey(String certId) {
        X509Certificate cf = null;
        if (certMap.containsKey(certId)) {
            // The certificate object corresponding to the certID exists.
            cf = certMap.get(certId);
            return cf.getPublicKey();
        } else {
            // Re-load the catalog of certificates if such certificate object does not exist.
            initValidateCertFromDir();
            if (certMap.containsKey(certId)) {
                // The certificate object corresponding to the certID exists.
                cf = certMap.get(certId);
                return cf.getPublicKey();
            } else {
                transactionLogger.error("There is a lack certId=[" + certId + "] of the corresponding certificate for verifying certificates. ");
                return null;
            }
        }
    }

    /**
     * Obtain the certID of the signed private key certificate configured in the configuration file acp_sdk.properties.
     *
     * @return Physical number of the certificate
     */
    public static String getSignCertId() {
        try {
            Enumeration<String> aliasenum = keyStore.aliases();
            String keyAlias = null;
            if (aliasenum.hasMoreElements()) {
                keyAlias = aliasenum.nextElement();
            }
            X509Certificate cert = (X509Certificate) keyStore
                    .getCertificate(keyAlias);
            return cert.getSerialNumber().toString();
        } catch (Exception e) {
            transactionLogger.error("getSignCertId Error" + e);
            return null;
        }
    }

    /**
     * Obtain the certId of the encryption certificate for sensitive information
     *
     * @return
     */
    public static String getEncryptCertId() {
        if (null == encryptCert) {
            String path = SDKConfig.getConfig().getEncryptCertPath();

            transactionLogger.error("ENc Path from property---" + path);

            if (!isEmpty(path)) {
                encryptCert = initCert(path);
                return encryptCert.getSerialNumber().toString();
            } else {
                transactionLogger.error("acpsdk.encryptCert.path is empty");
                return null;
            }
        } else {
            return encryptCert.getSerialNumber().toString();
        }
    }

    /**
     * Read the signed private key certificate as the storage object of the certificate.
     *
     * @param pfxkeyfile
     *            Certificate name
     * @param keypwd
     *            Certificate password
     * @param type
     *            Certificate type
     * @return Certificate object
     * @throws IOException
     */
    private static KeyStore getKeyInfo(String pfxkeyfile, String keypwd,
                                       String type) throws IOException {
        transactionLogger.error("Load signed certificates==>" + pfxkeyfile);
        FileInputStream fis = null;
        try {
            KeyStore ks = KeyStore.getInstance(type, "BC");
            transactionLogger.error("Load RSA CertPath=[" + pfxkeyfile + "],Pwd=[" + keypwd + "],type=[" + type + "]");

            fis = new FileInputStream(pfxkeyfile);
            char[] nPassword = null;
            nPassword = null == keypwd || "".equals(keypwd.trim()) ? null: keypwd.toCharArray();

            if (null != ks)
            {
                ks.load(fis, nPassword);
            }
            return ks;
        } catch (Exception e) {
            transactionLogger.error("getKeyInfo Error---" + e.getMessage());
            return null;
        } finally {
            if(null!=fis)
                fis.close();
        }
    }

    /**
     * Obtain the certId of the signed private key certificate by means of its path and password.
     * @param certPath
     * @param certPwd
     * @return
     */
    public static String getCertIdByKeyStoreMap(String certPath, String certPwd) {
        if (!keyStoreMap.containsKey(certPath)) {
            // If such certID is not found in the cache, load an RSA certificate.
            loadSignCert(certPath, certPwd);
        }
        return getCertIdIdByStore(keyStoreMap.get(certPath));
    }

    /**
     * Obtain the certID value of the private key certificate by means of keyStore.
     * @param keyStore
     * @return
     */
    private static String getCertIdIdByStore(KeyStore keyStore) {
        Enumeration<String> aliasenum = null;
        try {
            aliasenum = keyStore.aliases();
            String keyAlias = null;
            if (aliasenum.hasMoreElements()) {
                keyAlias = aliasenum.nextElement();
            }
            X509Certificate cert = (X509Certificate) keyStore
                    .getCertificate(keyAlias);
            return cert.getSerialNumber().toString();
        } catch (KeyStoreException e) {
            transactionLogger.error("getCertIdIdByStore Error" + e);
            return null;
        }
    }

    /**
     * Generate an RSA public key by means of module and exponent. Note: This code uses RSA/None/PKCS1Padding as the default padding method. The default padding methods for different JDKs may be different.
     *
     * @param modulus
     *            Module
     * @param exponent
     *            Exponent
     * @return
     */
    private static PublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            transactionLogger.error("Fail to construct an RSA public key:" + e);
            return null;
        }
    }

    /**
     * Convert the string into X509Certificate object.
     *
     * @param x509CertString
     * @return
     */
    public static X509Certificate genCertificateByStr(String x509CertString) {
        X509Certificate x509Cert = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
            InputStream tIn = new ByteArrayInputStream(
                    x509CertString.getBytes("ISO-8859-1"));
            x509Cert = (X509Certificate) cf.generateCertificate(tIn);
        } catch (Exception e) {
            transactionLogger.error("gen certificate error" + e);
        }
        return x509Cert;
    }

    /**
     * Obtain the intermediate certificate used to conduct signature authentication for public keys from the configuration file acp_sdk.properties.
     * @return
     */
    public static X509Certificate getMiddleCert() {
        if (null == middleCert) {
            String path = SDKConfig.getConfig().getMiddleCertPath();
            if (!isEmpty(path)) {
                initMiddleCert();
            } else {
                transactionLogger.error(SDKConfig.SDK_MIDDLECERT_PATH + " not set in " + SDKConfig.FILE_NAME);
                return null;
            }
        }
        return middleCert;
    }

    /**
     * Obtain the root certificate used to conduct signature authentication for public keys from the configuration file acp_sdk.properties.
     * @return
     */
    public static X509Certificate getRootCert() {
        if (null == rootCert) {
            String path = SDKConfig.getConfig().getRootCertPath();
            if (!isEmpty(path)) {
                initRootCert();
            } else {
                transactionLogger.error(SDKConfig.SDK_ROOTCERT_PATH + " not set in " + SDKConfig.FILE_NAME);
                return null;
            }
        }
        return rootCert;
    }

    /**
     * Obtain the CN of the certificate
     * @param aCert
     * @return
     */
    private static String getIdentitiesFromCertficate(X509Certificate aCert) {
        String tDN = aCert.getSubjectDN().toString();
        String tPart = "";
        if ((tDN != null)) {
            String tSplitStr[] = tDN.substring(tDN.indexOf("CN=")).split("@");
            if (tSplitStr != null && tSplitStr.length > 2
                    && tSplitStr[2] != null)
                tPart = tSplitStr[2];
        }
        return tPart;
    }

    /**
     * Verify the certificate chain.
     * @param cert
     * @return
     */
    private static boolean verifyCertificateChain(X509Certificate cert){

        if ( null == cert) {
            transactionLogger.error("cert must Not null");
            return false;
        }

        X509Certificate middleCert = CertUtil.getMiddleCert();
        if (null == middleCert) {
            transactionLogger.error("middleCert must Not null");
            return false;
        }

        X509Certificate rootCert = CertUtil.getRootCert();
        if (null == rootCert) {
            transactionLogger.error("rootCert or cert must Not null");
            return false;
        }

        try {

            X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(cert);

            Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
            trustAnchors.add(new TrustAnchor(rootCert, null));
            PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(
                    trustAnchors, selector);

            Set<X509Certificate> intermediateCerts = new HashSet<X509Certificate>();
            intermediateCerts.add(rootCert);
            intermediateCerts.add(middleCert);
            intermediateCerts.add(cert);

            pkixParams.setRevocationEnabled(false);

            CertStore intermediateCertStore = CertStore.getInstance("Collection",
                    new CollectionCertStoreParameters(intermediateCerts), "BC");
            pkixParams.addCertStore(intermediateCertStore);

            CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", "BC");

            @SuppressWarnings("unused")
            PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) builder
                    .build(pkixParams);
            transactionLogger.error("verify certificate chain succeed.");
            return true;
        } catch (java.security.cert.CertPathBuilderException e){
            transactionLogger.error("verify certificate chain fail." + e);
        } catch (Exception e) {
            transactionLogger.error("verify certificate chain exception: " + e);
        }
        return false;
    }

    /**
     * Check the certificate chain.
     *
     * //@param rootCerts
     *            Root certificates
     * @param cert
     *            Certificates to be authenticated
     * @return
     */
    public static boolean verifyCertificate(X509Certificate cert) {

        if ( null == cert) {
            transactionLogger.error("cert must Not null");
            return false;
        }
        try {
            cert.checkValidity();//Authentication expiry date
//			cert.verify(middleCert.getPublicKey());
            if(!verifyCertificateChain(cert)){
                return false;
            }
        } catch (Exception e) {
            transactionLogger.error("verifyCertificate fail" + e);
            return false;
        }

        if(SDKConfig.getConfig().isIfValidateCNName()){
            // Check whether the public key belongs to China UnionPay.
            if(!UNIONPAY_CNNAME.equals(CertUtil.getIdentitiesFromCertficate(cert))) {
                transactionLogger.error("cer owner is not CUP:" + CertUtil.getIdentitiesFromCertficate(cert));
                return false;
            }
        } else {
            // Check whether the public key belongs to China UnionPay.
            if(!UNIONPAY_CNNAME.equals(CertUtil.getIdentitiesFromCertficate(cert))
                    && !"00040000:SIGN".equals(CertUtil.getIdentitiesFromCertficate(cert))) {
                transactionLogger.error("cer owner is not CUP:" + CertUtil.getIdentitiesFromCertficate(cert));
                return false;
            }
        }
        return true;
    }

    /**
     * Print the system environment information
     */
    private static void printSysInfo() {
        transactionLogger.error("================= SYS INFO begin====================");
        transactionLogger.error("os_name:" + System.getProperty("os.name"));
        transactionLogger.error("os_arch:" + System.getProperty("os.arch"));
        transactionLogger.error("os_version:" + System.getProperty("os.version"));
        transactionLogger.error("java_vm_specification_version:"
                + System.getProperty("java.vm.specification.version"));
        transactionLogger.error("java_vm_specification_vendor:"
                + System.getProperty("java.vm.specification.vendor"));
        transactionLogger.error("java_vm_specification_name:"
                + System.getProperty("java.vm.specification.name"));
        transactionLogger.error("java_vm_version:"
                + System.getProperty("java.vm.version"));
        transactionLogger.error("java_vm_name:" + System.getProperty("java.vm.name"));
        transactionLogger.error("java.version:" + System.getProperty("java.version"));
        transactionLogger.error("java.vm.vendor=[" + System.getProperty("java.vm.vendor") + "]");
        transactionLogger.error("java.version=[" + System.getProperty("java.version") + "]");
        printProviders();
        transactionLogger.error("================= SYS INFO end=====================");
    }

    /**
     * Print the list of algorithm providers
     */
    private static void printProviders() {
        transactionLogger.error("Providers List:");
        Provider[] providers = Security.getProviders();
        for (int i = 0; i < providers.length; i++) {
            transactionLogger.error(i + 1 + "." + providers[i].getName());
        }
    }

    /**
     * Certificate file filter
     *
     */
    static class CerFilter implements FilenameFilter {
        public boolean isCer(String name) {
            if (name.toLowerCase().endsWith(".cer")) {
                return true;
            } else {
                return false;
            }
        }
        public boolean accept(File dir, String name) {
            return isCer(name);
        }
    }

}
