package com.directi.pg;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.HTTPUtilities;
import org.owasp.esapi.Logger;
import org.owasp.esapi.User;
import org.owasp.esapi.reference.DefaultEncoder;
import org.owasp.esapi.errors.*;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import java.util.Locale;
import java.util.Collections;
import java.util.HashMap;
/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Feb 27, 2012
 * Time: 10:07:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultUser implements User, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The idle timeout length specified in the ESAPI config file. */
	private static final int IDLE_TIMEOUT_LENGTH = ESAPI.securityConfiguration().getSessionIdleTimeoutLength();

	/** The absolute timeout length specified in the ESAPI config file. */
	private static final int ABSOLUTE_TIMEOUT_LENGTH = ESAPI.securityConfiguration().getSessionAbsoluteTimeoutLength();

	/** The logger used by the class. */
	private transient final Logger logger = ESAPI.getLogger("DefaultUser");

	/** This user's account id. */
	long accountId = 0;

	/** This user's account name. */
	private String accountName = "";

	/** This user's screen name (account name alias). */
	private String screenName = "";

	/** This user's CSRF token. */
	private String csrfToken = resetCSRFToken();

	/** This user's assigned roles. */
	private Set<String> roles = new HashSet<String>();

	/** Whether this user's account is locked. */
	private boolean locked = false;

	/** Whether this user is logged in. */
	private boolean loggedIn = true;

    /** Whether this user's account is enabled. */
	private boolean enabled = true;

    /** The last host address used by this user. */
    private String lastHostAddress;

	/** The last password change time for this user. */
	private Date lastPasswordChangeTime = new Date(0);

	/** The last login time for this user. */
	private Date lastLoginTime = new Date(0);

	/** The last failed login time for this user. */
	private Date lastFailedLoginTime = new Date(0);

	/** The expiration date/time for this user's account. */
	private Date expirationTime = new Date(Long.MAX_VALUE);

	/** The sessions this user is associated with */
	private transient Set<HttpSession> sessions = new HashSet<HttpSession>();

	/** The event map for this User */
	private transient HashMap eventMap = new HashMap();

	/* A flag to indicate that the password must be changed before the account can be used. */
	// private boolean requiresPasswordChange = true;

	/** The failed login count for this user's account. */
	private int failedLoginCount = 0;

	/** This user's Locale. */
	private Locale locale;

    private static final int MAX_ROLE_LENGTH = 250;

	/**
	 * Instantiates a new user.
	 *
	 * @param accountName
	 * 		The name of this user's account.
	 */
	public DefaultUser(String accountName) {
		this.accountName = accountName.toLowerCase();
		while( true ) {
			long id = Math.abs( ESAPI.randomizer().getRandomLong() );
			if ( ESAPI.authenticator().getUser( id ) == null && id != 0 ) {
				this.accountId = id;
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRole(String role) throws AuthenticationException {
		String roleName = role.toLowerCase();
		if ( ESAPI.validator().isValidInput("addRole", roleName, "RoleName", MAX_ROLE_LENGTH, false) ) {
			roles.add(roleName);
			logger.info(Logger.SECURITY_SUCCESS, "Role " + roleName + " added to " + getAccountName() );
		} else {
			throw new AuthenticationAccountsException( "Add role failed", "Attempt to add invalid role " + roleName + " to " + getAccountName() );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRoles(Set<String> newRoles) throws AuthenticationException {
        for (String newRole : newRoles)
        {
            addRole(newRole);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void changePassword(String oldPassword, String newPassword1, String newPassword2) throws AuthenticationException, EncryptionException {
		ESAPI.authenticator().changePassword(this, oldPassword, newPassword1, newPassword2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void disable() {
		enabled = false;
		logger.info( Logger.SECURITY_SUCCESS, "Account disabled: " + getAccountName() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void enable() {
		this.enabled = true;
		logger.info( Logger.SECURITY_SUCCESS, "Account enabled: " + getAccountName() );
	}

	/**
	 * {@inheritDoc}
	 */
    public long getAccountId() {
        return accountId;
    }

	/**
	 * {@inheritDoc}
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCSRFToken() {
		return csrfToken;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getExpirationTime() {
		return (Date)expirationTime.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFailedLoginCount() {
		return failedLoginCount;
	}

	/**
	 * Set the failed login count
	 *
	 * @param count
	 * 			the number of failed logins
	 */
	void setFailedLoginCount(int count) {
		logger.debug(Logger.SECURITY_FAILURE,"========="+count);
		failedLoginCount = count;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getLastFailedLoginTime() {
		return (Date)lastFailedLoginTime.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLastHostAddress() {
		if ( lastHostAddress == null ) {
			return "unknown";
		}
        return lastHostAddress;
    }

	/**
	 * {@inheritDoc}
	 */
	public Date getLastLoginTime() {
		return (Date)lastLoginTime.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getLastPasswordChangeTime() {
		return (Date)lastPasswordChangeTime.clone();
	}

	/**
	 * {@inheritDoc}
     *
     * @return
     */
	public String getName() {
		return this.getAccountName();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<String> getRoles() {
		return Collections.unmodifiableSet(roles);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getScreenName() {
		return screenName;
	}

	/**
	 * {@inheritDoc}
	 */
    public void addSession( HttpSession s ) {
        sessions.add( s );
    }

	/**
	 * {@inheritDoc}
	 */
    public void removeSession( HttpSession s ) {
        sessions.remove( s );
    }

	/**
	 * {@inheritDoc}
     *
     * @return
     */
	public Set getSessions() {
	    return sessions;
	}

	/**
	 * {@inheritDoc}
	 */
	public void incrementFailedLoginCount() {
		failedLoginCount++;
		String failCounter = String.valueOf(failedLoginCount);
		logger.debug(Logger.SECURITY_FAILURE,"Login Failed Counter---"+ failCounter);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAnonymous() {
		// User cannot be anonymous, since we have a special User.ANONYMOUS instance
		// for the anonymous user
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isExpired() {
		return getExpirationTime().before( new Date() );

		// If expiration should happen automatically or based on lastPasswordChangeTime?
		//		long from = lastPasswordChangeTime.getTime();
		//		long to = new Date().getTime();
		//		double difference = to - from;
		//		long days = Math.round((difference / (1000 * 60 * 60 * 24)));
		//		return days > 60;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInRole(String role) {
		return roles.contains(role.toLowerCase());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSessionAbsoluteTimeout() {
		HttpSession session = ESAPI.httpUtilities().getCurrentRequest().getSession(false);
		if ( session == null ) return true;
		Date deadline = new Date( session.getCreationTime() + ABSOLUTE_TIMEOUT_LENGTH);
		Date now = new Date();
		return now.after(deadline);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSessionTimeout() {
		HttpSession session = ESAPI.httpUtilities().getCurrentRequest().getSession(false);
		if ( session == null ) return true;
		Date deadline = new Date( session.getLastAccessedTime() + IDLE_TIMEOUT_LENGTH);
		Date now = new Date();
		return now.after(deadline);
	}

	/**
	 * {@inheritDoc}
	 */
	public void lock() {
		this.locked = true;
		logger.info(Logger.SECURITY_SUCCESS, "Account locked: " + getAccountName() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void loginWithPassword(String password) throws AuthenticationException {
		if ( password == null || password.equals("") ) {
			setLastFailedLoginTime(new Date());
			incrementFailedLoginCount();
			throw new AuthenticationLoginException( "Login failed", "Missing password: " + accountName  );
		}

		// don't let disabled users log in
		if ( !isEnabled() ) {
			setLastFailedLoginTime(new Date());
			incrementFailedLoginCount();
			throw new AuthenticationLoginException("Login failed :Account Disabled", "Disabled user attempt to login: " + accountName );
		}

		// don't let locked users log in
		if ( isLocked() ) {
			setLastFailedLoginTime(new Date());
			incrementFailedLoginCount();
			throw new AuthenticationLoginException("Login failed :Account Locked", "Locked user attempt to login: " + accountName );
		}

		// don't let expired users log in
//		if ( isExpired() ) {
//			setLastFailedLoginTime(new Date());
//			incrementFailedLoginCount();
//			throw new AuthenticationLoginException("Login failed", "Expired user attempt to login: " + accountName );
//		}

		logout();

		if ( verifyPassword( password ) ) {
			logger.info(Logger.SECURITY_FAILURE,"inside if verify password-----");
			loggedIn = true;
			ESAPI.httpUtilities().changeSessionIdentifier( ESAPI.currentRequest() );
			setLastLoginTime(new Date());
            setLastHostAddress( ESAPI.httpUtilities().getCurrentRequest().getRemoteAddr() );
            setFailedLoginCount(0);
            ESAPI.authenticator().setCurrentUser(this);
			logger.info(Logger.SECURITY_SUCCESS, "User logged in: " + accountName );
		} else {
			logger.info(Logger.SECURITY_FAILURE,"inside else verify password-----");
			loggedIn = false;
			setLastFailedLoginTime(new Date());
			incrementFailedLoginCount();
			logger.info(Logger.SECURITY_FAILURE, "User failed login counter==== " + getFailedLoginCount());
			logger.info(Logger.SECURITY_FAILURE, "User getAllowedLoginAttempts==== " + ESAPI.securityConfiguration().getAllowedLoginAttempts());
			if (getFailedLoginCount() >= ESAPI.securityConfiguration().getAllowedLoginAttempts()) {
				lock();
			}
            try
            {
             ((PzAuthenticator)ESAPI.authenticator()).updateUser(this);
            }
            catch(Exception e)
            {
               logger.error(Logger.SECURITY_FAILURE,"Unable to save user throwing SQL Exception as System Error : ",e);
            }
            logger.info(Logger.SECURITY_FAILURE, "Incorrect password provided for: " + accountName );
			throw new AuthenticationLoginException("Login failed", "Incorrect password provided for " + getAccountName() );
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public void logout() {
		ESAPI.httpUtilities().killCookie( ESAPI.currentRequest(), ESAPI.currentResponse(), HTTPUtilities.REMEMBER_TOKEN_COOKIE_NAME );

		HttpSession session = ESAPI.currentRequest().getSession(false);
		if (session != null) {
            removeSession(session);
			session.invalidate();
		}
		ESAPI.httpUtilities().killCookie(ESAPI.currentRequest(), ESAPI.currentResponse(), ESAPI.securityConfiguration().getHttpSessionIdName());
		loggedIn = false;
		logger.info(Logger.SECURITY_SUCCESS, "Logout successful" );

        User user =ESAPI.authenticator().getCurrentUser();

        logger.info(Logger.SECURITY_SUCCESS, "user logged out  "+user.getAccountName() );
        if (user != null && !user.isAnonymous())
        {

            try
            {
             ((PzAuthenticator)ESAPI.authenticator()).updateUser(this);
            }
            catch(Exception e)
            {
               logger.error(Logger.SECURITY_FAILURE,"Unable to save user throwing SQL Exception as System Error : ",e);
            }
        }

		ESAPI.authenticator().setCurrentUser(User.ANONYMOUS);

	}

	/**
	 * {@inheritDoc}
	 */
	public void removeRole(String role) {
		roles.remove(role.toLowerCase());
		logger.info(Logger.SECURITY_SUCCESS, "Role " + role + " removed from " + getAccountName() );
	}

	/**
	 * {@inheritDoc}
	 *
	 * In this implementation, we have chosen to use a random token that is
	 * stored in the User object. Note that it is possible to avoid the use of
	 * server side state by using either the hash of the users's session id or
	 * an encrypted token that includes a timestamp and the user's IP address.
	 * user's IP address. A relatively short 8 character string has been chosen
	 * because this token will appear in all links and forms.
	 *
	 * @return the string
	 */
	public String resetCSRFToken() {
		// user.csrfToken = ESAPI.encryptor().hash( session.getId(),user.name );
		// user.csrfToken = ESAPI.encryptor().encrypt( address + ":" + ESAPI.encryptor().getTimeStamp();
		csrfToken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
		return csrfToken;
	}

	/**
	 * Sets the account id for this user's account.
	 */
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}


	/**
	 * {@inheritDoc}
	 */
	public void setAccountName(String accountName) {
		String old = getAccountName();
		this.accountName = accountName.toLowerCase();
		if (old != null) {
			if ( old.equals( "" ) ) {
				old = "[nothing]";
			}
			logger.info(Logger.SECURITY_SUCCESS, "Account name changed from " + old + " to " + getAccountName() );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = new Date( expirationTime.getTime() );
		logger.info(Logger.SECURITY_SUCCESS, "Account expiration time set to " + expirationTime + " for " + getAccountName() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void setLastFailedLoginTime(Date lastFailedLoginTime) {
		this.lastFailedLoginTime = lastFailedLoginTime;
		logger.info(Logger.SECURITY_SUCCESS, "Set last failed login time to " + lastFailedLoginTime + " for " + getAccountName() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void setLastHostAddress(String remoteHost) throws AuthenticationHostException
    {
		/*if ( lastHostAddress != null &&!lastHostAddress.equals("") ) { //&& !lastHostAddress.equals(remoteHost)
        	// returning remote address not remote hostname to prevent DNS lookup
			throw new AuthenticationHostException("Host change", "User session just jumped from " + lastHostAddress + " to " + remoteHost );
		}*/

		lastHostAddress = remoteHost;
    }

	/**
	 * {@inheritDoc}
	 */
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
		logger.info(Logger.SECURITY_SUCCESS, "Set last successful login time to " + lastLoginTime + " for " + getAccountName() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void setLastPasswordChangeTime(Date lastPasswordChangeTime) {
		this.lastPasswordChangeTime = lastPasswordChangeTime;
		logger.info(Logger.SECURITY_SUCCESS, "Set last password change time to " + lastPasswordChangeTime + " for " + getAccountName() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRoles(Set<String> roles) throws AuthenticationException {
		this.roles = new HashSet<String>();
		addRoles(roles);
		logger.info(Logger.SECURITY_SUCCESS, "Adding roles " + roles + " to " + getAccountName() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void setScreenName(String screenName) {
		this.screenName = screenName;
		logger.info(Logger.SECURITY_SUCCESS, "ScreenName changed to " + screenName + " for " + getAccountName() );
	}

	/**
	 * {@inheritDoc}
     *
     * @return
     */
	public String toString() {
		StringBuffer roles= new StringBuffer();
		for(String role:getRoles())
		{
			if(roles.length()>0)
			{
				roles.append(",");
			}
			roles.append(role);
		}
		logger.info( Logger.SECURITY_SUCCESS, "USER STRING:::"+"USER:" + accountName+"|ROLE:"+roles );
		return "USER:" + accountName+"ROLE:"+roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public void unlock() {
		this.locked = false;
		this.failedLoginCount = 0;
		logger.info( Logger.SECURITY_SUCCESS, "Account unlocked: " + getAccountName() );
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean verifyPassword(String password) {
		return ESAPI.authenticator().verifyPassword(this, password);
	}

    /**
     * Override clone and make final to prevent duplicate user objects.
     * @return
     * @throws java.lang.CloneNotSupportedException
     */
    public final Object clone() throws java.lang.CloneNotSupportedException {
    	  throw new java.lang.CloneNotSupportedException();
    }
	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

    public HashMap getEventMap() {
    	return eventMap;
    }
	public boolean equals(Object obj)
	{
		//logger.info(Logger.SECURITY_SUCCESS, "this:::" + this + " actual Object " + obj );
		if(obj!=null && obj instanceof DefaultUser)
		{
			DefaultUser defaultUser = (DefaultUser) obj;
			if(this.accountId  == defaultUser.accountId)
			{
				return true;
			}
		}
		return false;
	}
	public int hashCode()
	{
		int result = 0;
		result = (int)accountId;
		return result;

	}
}
