package com.directi.pg;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.sql.*;

import org.owasp.esapi.Authenticator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.EncoderConstants;
import org.owasp.esapi.Logger;
import org.owasp.esapi.Randomizer;
import org.owasp.esapi.StringUtilities;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationAccountsException;
import org.owasp.esapi.errors.AuthenticationCredentialsException;
import org.owasp.esapi.errors.AuthenticationException;
import org.owasp.esapi.errors.EncryptionException;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Feb 25, 2012
 * Time: 11:49:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class PzAuthenticator extends AbstractAuthenticator
{
    private static volatile Authenticator singletonInstance;

           public static Authenticator getInstance()
           {
               if ( singletonInstance == null ) {
                   synchronized ( PzAuthenticator.class ) {
                       if ( singletonInstance == null ) {
                           singletonInstance = new PzAuthenticator();
                       }
                   }
               }
               return singletonInstance;
           }


           /**
            * The logger.
            */
           private final Logger logger = ESAPI.getLogger("Authenticator");



           /**
            * How frequently to check the user db for external modifications
            */
           private long checkInterval = 60 * 1000;

           /**
            * The last modified time we saw on the user db.
            */
           private long lastModified = 0;

           /**
            * The last time we checked if the user db had been modified externally
            */
           private long lastChecked = 0;

           private static final int MAX_ACCOUNT_NAME_LENGTH = 250;

           /**
            * Fail safe main program to add or update an account in an emergency.
            * <p/>
            * Warning: this method does not perform the level of validation and checks
            * generally required in ESAPI, and can therefore be used to create a username and password that do not comply
            * with the username and password strength requirements.
            * <p/>
            * Example: Use this to add the alice account with the admin role to the users file:
            * <PRE>
            * <p/>
            * java -Dorg.owasp.esapi.resources="/path/resources" -classpath esapi.jar org.owasp.esapi.Authenticator alice password admin
            * <p/>
            * </PRE>
            *
            * @param args the arguments (username, password, role)
            * @throws Exception the exception
            */
           public static void main(String[] args) throws Exception {
               if (args.length != 3) {
                   //System.out.println("Usage: Authenticator accountname password role");
                   return;
               }
               PzAuthenticator auth = new PzAuthenticator();
               String accountName = args[0].toLowerCase();
               String password = args[1];
               String role = args[2];
               DefaultUser user = (DefaultUser) auth.getUser(args[0]);
               if (user == null) {
                   user = new DefaultUser(accountName);
                   String newHash = auth.hashPassword(password, accountName);
                   auth.setHashedPassword(user, newHash);
                   //user.addRole(role);
                   user.enable();
                   user.unlock();
                   auth.userMap.put(user.getAccountId(), user);
                   //System.out.println("New user created: " + accountName);
                   auth.saveUser(user);
                   //System.out.println("User account " + user.getAccountName() + " updated");
               } else {
                   System.err.println("User account " + user.getAccountName() + " already exists!");
               }
           }

           /**
            * Add a hash to a User's hashed password list.  This method is used to store a user's old password hashes
            * to be sure that any new passwords are not too similar to old passwords.
            *
            * @param user the user to associate with the new hash
            * @param hash the hash to store in the user's password hash list
            */
           private void setHashedPassword(User user, String hash) {
               List<String> hashes = getAllHashedPasswords(user, true);
               hashes.add(0, hash);
               if (hashes.size() > ESAPI.securityConfiguration().getMaxOldPasswordHashes()) {
                   hashes.remove(hashes.size() - 1);
               }
               logger.info(Logger.SECURITY_SUCCESS, "New hashed password stored for " + user.getAccountName());
           }

           /**
            * Return the specified User's current hashed password.
            *
            * @param user this User's current hashed password will be returned
            * @return the specified User's current hashed password
            */
           String getHashedPassword(User user) {
               List hashes = getAllHashedPasswords(user, false);
               return (String) hashes.get(0);
           }

           /**
            * Set the specified User's old password hashes.  This will not set the User's current password hash.
            *
            * @param user      the User whose old password hashes will be set
            * @param oldHashes a list of the User's old password hashes     *
            */
           void setOldPasswordHashes(User user, List<String> oldHashes) {
               List<String> hashes = getAllHashedPasswords(user, true);
               if (hashes.size() > 1) {
                   hashes.removeAll(hashes.subList(1, hashes.size() - 1));
               }
               hashes.addAll(oldHashes);
           }

           /**
            * Returns all of the specified User's hashed passwords.  If the User's list of passwords is null,
            * and create is set to true, an empty password list will be associated with the specified User
            * and then returned. If the User's password map is null and create is set to false, an exception
            * will be thrown.
            *
            * @param user   the User whose old hashes should be returned
            * @param create true - if no password list is associated with this user, create one
            *               false - if no password list is associated with this user, do not create one
            * @return a List containing all of the specified User's password hashes
            */
           List<String> getAllHashedPasswords(User user, boolean create) {
               List<String> hashes = passwordMap.get(user);
               if (hashes != null) {
                   return hashes;
               }
               if (create) {
                   hashes = new ArrayList<String>();
                   passwordMap.put(user, hashes);
                   return hashes;
               }
               throw new RuntimeException("No hashes found for " + user.getAccountName() + ". Is User.hashcode() and equals() implemented correctly?");
           }

           /**
            * Get a List of the specified User's old password hashes.  This will not return the User's current
            * password hash.
            *
            * @param user he user whose old password hashes should be returned
            * @return the specified User's old password hashes
            */
           List<String> getOldPasswordHashes(User user) {
               List<String> hashes = getAllHashedPasswords(user, false);
               if (hashes.size() > 1) {
                   return Collections.unmodifiableList(hashes.subList(1, hashes.size()));
               }
               return Collections.emptyList();
           }

           /**
            * The user map.
            */
           private Map<Long, User> userMap = new HashMap<Long, User>();

           // Map<User, List<String>>, where the strings are password hashes, with the current hash in entry 0
           private Map<User, List<String>> passwordMap = new HashMap<User, List<String>>();



           /**
            *
            */
           private PzAuthenticator() {
               super();
           }


           /**
            * {@inheritDoc}
            */
           public synchronized User createUser(String accountName, String password1, String role) throws AuthenticationException {
               loadUsersIfNecessary();
               if (accountName == null) {
                   throw new AuthenticationAccountsException("Account creation failed", "Attempt to create user with null accountName");
               }
               if (getUser(accountName) != null) {
                   throw new AuthenticationAccountsException("Account creation failed", "Duplicate user creation denied for " + accountName);
               }

               if (role.equalsIgnoreCase("merchant"))
               {
                   verifyMerchantAccountNameStrength(accountName);
               }
               else
               {
                   verifyAccountNameStrength(accountName);
               }

               if (password1 == null) {
                   throw new AuthenticationCredentialsException("Invalid account name", "Attempt to create account " + accountName + " with a null password");
               }

               DefaultUser user = new DefaultUser(accountName);

               verifyPasswordStrength(null, password1, user);

              /*if (!password1.equals(password2)) {
                   throw new AuthenticationCredentialsException("Passwords do not match", "Passwords for " + accountName + " do not match");
               }
                */

               user.addRole(role);

               try {
                   setHashedPassword(user, hashPassword(password1, accountName));
               } catch (EncryptionException ee) {
                   throw new AuthenticationException("Internal error", "Error hashing password for " + accountName, ee);
               }
               userMap.put(user.getAccountId(), user);
               logger.info(Logger.SECURITY_SUCCESS, "New user created: " + accountName);
               saveUser(user);
               return user;
           }

           /**
            * {@inheritDoc}
            */
           public String generateStrongPassword() {
               return generateStrongPassword("");
           }

           /**
            * Generate a strong password that is not similar to the specified old password.
            *
            * @param oldPassword the password to be compared to the new password for similarity
            * @return a new strong password that is dissimilar to the specified old password
            */
           private String generateStrongPassword(String oldPassword) {
               Randomizer r = ESAPI.randomizer();
               int letters = r.getRandomInteger(4, 6);  // inclusive, exclusive
               int digits = 7 - letters;
               String passLetters = r.getRandomString(letters, EncoderConstants.CHAR_PASSWORD_LETTERS);
               String passDigits = r.getRandomString(digits, EncoderConstants.CHAR_PASSWORD_DIGITS);
               String passSpecial = r.getRandomString(1, EncoderConstants.CHAR_PASSWORD_SPECIALS);
               String newPassword = passLetters + passSpecial + passDigits;
               if (StringUtilities.getLevenshteinDistance(oldPassword, newPassword) > 5) {
                   return newPassword;
               }
               return generateStrongPassword(oldPassword);
           }

           /**
            * {@inheritDoc}
            */
           public void changePassword(User user, String currentPassword,
                                      String newPassword, String newPassword2)
                   throws AuthenticationException {
                   String accountName = user.getAccountName();
               try {
                   String currentHash = getHashedPassword(user);
                   if(currentPassword!=null)
                   {
                       String verifyHash = hashPassword(currentPassword, accountName);
                       if (!currentHash.equals(verifyHash)) {
                           throw new AuthenticationCredentialsException("Password MISMATCH", "Authentication failed for password change on user: " + accountName);
                       }
                   }

                   if (newPassword == null || newPassword2 == null || !newPassword.equals(newPassword2)) {
                       throw new AuthenticationCredentialsException("Password change failed", "Passwords do not match for password change on user: " + accountName);
                   }
                   if(currentPassword!=null)
                   {
                       verifyPasswordStrength(null, newPassword, user);
                   }
                   user.setLastPasswordChangeTime(new Date());
                   String newHash = hashPassword(newPassword, accountName);
                   if(currentPassword!=null)
                   {
                       if (getOldPasswordHashes(user).contains(newHash) || currentPassword.equals(newPassword))
                       {
                           throw new AuthenticationCredentialsException("Password change failed", "Password change matches a recent password for user: " + accountName);
                       }
                   }
                   else
                   {
                       if (getOldPasswordHashes(user).contains(newHash)) {
                           throw new AuthenticationCredentialsException("Password change failed", "Password change matches a recent password for user: " + accountName);
                       }
                   }
                   setHashedPassword(user, newHash);
                   logger.info(Logger.SECURITY_SUCCESS, "Password changed for user: " + accountName);
                   // jtm - 11/2/2010 - added to resolve http://code.google.com/p/owasp-esapi-java/issues/detail?id=13
                   updateUser(user);
               } catch (EncryptionException ee) {
                   throw new AuthenticationException("Password change failed", "Encryption exception changing password for " + accountName, ee);
               }
           }


     /**
            * {@inheritDoc}
            */
           public void forgetPassword(User user, String currentPasswordHash, String newPasswordHash, String newPassword2Hash)
                   throws AuthenticationException {
                   String accountName = user.getAccountName();
               try {
                   String currentHash = getHashedPassword(user);

                   /*if (!currentHash.equals(currentPasswordHash)) {
                       throw new AuthenticationCredentialsException("Password change failed", "Authentication failed for password change on user: " + accountName);
                   }*/
                   if (newPasswordHash == null || newPassword2Hash == null || !newPasswordHash.equals(newPassword2Hash)) {
                       throw new AuthenticationCredentialsException("Password change failed", "Passwords do not match for password change on user: " + accountName);
                   }

                   user.setLastPasswordChangeTime(new Date());

                   if (getOldPasswordHashes(user).contains(newPasswordHash)) {
                       throw new AuthenticationCredentialsException("Password change failed", "Password change matches a recent password for user: " + accountName);
                   }
                   setHashedPassword(user, newPasswordHash);
                   logger.info(Logger.SECURITY_SUCCESS, "Password changed for user: " + accountName);
                   // jtm - 11/2/2010 - added to resolve http://code.google.com/p/owasp-esapi-java/issues/detail?id=13
                   updateUser(user);
               } catch (Exception ee) {
                   throw new AuthenticationException("Password change failed", "Encryption exception changing password for " + accountName, ee);
               }
           }

           /**
            * {@inheritDoc}
            */
           public boolean verifyPassword(User user, String password) {
               String accountName = user.getAccountName();
               try {
                   String hash = hashPassword(password, accountName);
                   String currentHash = getHashedPassword(user);
                   if (hash.equals(currentHash)) {
                       user.setLastLoginTime(new Date());
                       ((DefaultUser) user).setFailedLoginCount(0);
                       logger.info(Logger.SECURITY_SUCCESS, "Password verified for " + accountName);
                       return true;
                   }
               } catch (EncryptionException e) {
                   logger.fatal(Logger.SECURITY_FAILURE, "Encryption error verifying password for " + accountName);
               }
               logger.fatal(Logger.SECURITY_FAILURE, "Password verification failed for " + accountName);
               return false;
           }

           /**
            * {@inheritDoc}
            */
           public String generateStrongPassword(User user, String oldPassword) {
               String newPassword = generateStrongPassword(oldPassword);
               if (newPassword != null) {
                   logger.info(Logger.SECURITY_SUCCESS, "Generated strong password for " + user.getAccountName());
               }
               return newPassword;
           }

           /**
            * {@inheritDoc}
            */
           public synchronized User getUser(long accountId) {
               if (accountId == 0) {
                   return User.ANONYMOUS;
               }
               loadUsersIfNecessary();
               return userMap.get(accountId);
           }

           /**
            * {@inheritDoc}
            */
           public synchronized User getUser(String accountName) {
               if (accountName == null) {
                   return User.ANONYMOUS;
               }
               loadUsersIfNecessary();

               //Added for role check
               String role= null;

               if(ESAPI.httpUtilities().getCurrentRequest().getAttribute("role")!=null)
               {
                   role = (String)ESAPI.httpUtilities().getCurrentRequest().getAttribute("role");

               }

               Connection con=null;
               String query="select accountid from `user` where login=? and roles=?";
               try
               {
                   con=Database.getConnection();
                   PreparedStatement ps=con.prepareStatement(query.toString());
                   ps.setString(1,accountName);
                   ps.setString(2,role);
                   ResultSet rs=ps.executeQuery();
                   if(rs.next())
                   {
                       return userMap.get(rs.getLong(1));
                   }
               }
               catch (SystemError systemError)
               {
                   //To change body of catch statement use File | Settings | File Templates.
                   logger.fatal(Logger.SECURITY_FAILURE, " "+accountName+" ROLES::"+role);
                   return null;
               }
               catch (SQLException e)
               {
                   /*e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.*/
                   logger.fatal(Logger.SECURITY_FAILURE, " "+accountName+" ROLES::"+role);
                   return null;
               }
               finally {
                   Database.closeConnection(con);
               }
               return null;

           }



           /**
            * {@inheritDoc}
            */
           public synchronized Set getUserNames() {
               loadUsersIfNecessary();
               HashSet<String> results = new HashSet<String>();
               for (User u : userMap.values()) {
                   results.add(u.getAccountName());
               }
               return results;
           }

           /**
            * {@inheritDoc}
            *
            * @throws EncryptionException
            */
           public String hashPassword(String password, String accountName) throws EncryptionException {
               String salt = accountName.toLowerCase();
               return ESAPI.encryptor().hash(password, salt);
           }

           /**
            * Load users if they haven't been loaded in a while.
            */
           protected void loadUsersIfNecessary() {

               // We only check at most every checkInterval milliseconds
               long now = System.currentTimeMillis();
               if (now - lastChecked < checkInterval) {
                   logger.info(Logger.SECURITY_SUCCESS, "Not Loading users from  database", null);
                   return;
               }
               lastChecked = now;

               loadUsersImmediately();
           }


           /**
            *
            */
           protected void loadUsersImmediately() {
               synchronized (this) {


                   Connection conn = null;

                   try
                   {
                       HashMap<Long, User> map = new HashMap<Long, User>();
                       passwordMap = new HashMap<User, List<String>>();

                       conn = Database.getConnection();
                       String query = "select * from user";

                       PreparedStatement p1=conn.prepareStatement(query);
                       ResultSet result = p1.executeQuery();

                      while (result.next())
                       {
                           String line = format(result);
                           if (line!=null && line.length() > 0 ) {
                               DefaultUser user = createUser(line);
                               if (map.containsKey(new Long(user.getAccountId()))) {
                                   logger.fatal(Logger.SECURITY_FAILURE, "Problem in user database. Skipping duplicate user: " + user, null);
                               }
                               map.put(user.getAccountId(), user);
                           }
                       }

                       userMap = map;
                       this.lastModified = System.currentTimeMillis();
                       logger.info(Logger.SECURITY_SUCCESS, "User file reloaded: " + map.size(), null);
                   }
                   catch (SQLException e)
                   {
                       logger.error(Logger.SECURITY_FAILURE,"Unable to load users throwing SQL Exception as System Error : ",e);

                   }
                   catch(Exception e)
                   {
                       logger.error(Logger.SECURITY_FAILURE,"Unable to load users throwing  Exception as System Error : ",e);
                   }
                   finally
                   {
                       Database.closeConnection(conn);
                   }


               }
           }

           /*
            * Create a new user with all attributes from a String.  The format is:
            * accountId | accountName | password | roles (comma separated) | unlocked | enabled | old password hashes (comma separated) | last host address | last password change time | last long time | last failed login time | expiration time | failed login count
            * This method verifies the account name and password strength, creates a new CSRF token, then returns the newly created user.
            *
            * @param line parameters to set as attributes for the new User.
            * @return the newly created User
            * @throws AuthenticationException
            */
           private DefaultUser createUser(String line) throws AuthenticationException {


               String[] parts = line.split(" *\\| *");
               String accountIdString = parts[0];
               long accountId = Long.parseLong(accountIdString);
               String accountName = parts[1];

               verifyAccountNameStrength(accountName);
               DefaultUser user = new DefaultUser(accountName);
               user.accountId = accountId;

               String password = parts[2];
               verifyPasswordStrength(null, password, user);
               setHashedPassword(user, password);

               String[] roles = parts[3].toLowerCase().split(" *, *");
               for (String role : roles) {
                   if (!"".equals(role)) {
                       user.addRole(role);
                   }
               }
               if (!"unlocked".equalsIgnoreCase(parts[4])) {
                   user.lock();
               }
               if ("enabled".equalsIgnoreCase(parts[5])) {
                   user.enable();
               } else {
                   user.disable();
               }

               // generate a new csrf token
               user.resetCSRFToken();

               setOldPasswordHashes(user, Arrays.asList(parts[6].split(" *, *")));
               user.setLastHostAddress("null".equals(parts[7]) ? null : parts[7]);
               user.setLastPasswordChangeTime(new Date(Long.parseLong(parts[8])));
               user.setLastLoginTime(new Date(Long.parseLong(parts[9])));
               user.setLastFailedLoginTime(new Date(Long.parseLong(parts[10])));
               user.setExpirationTime(new Date(Long.parseLong(parts[11])));
               user.setFailedLoginCount(Integer.parseInt(parts[12]));
               return user;
           }

           /**
            * {@inheritDoc}
            */
           public synchronized void removeUser(String accountName) throws AuthenticationException {
               loadUsersIfNecessary();
               User user = getUser(accountName);
               if (user == null) {
                   throw new AuthenticationAccountsException("Remove user failed", "Can't remove invalid accountName " + accountName);
               }
               userMap.remove(user.getAccountId());
               logger.info(Logger.SECURITY_SUCCESS, "Removing user " + user.getAccountName());
               passwordMap.remove(user);
               removeUsers(user);
           }


           /**
            *
            * @param user
            * @throws AuthenticationCredentialsException
            */
           public synchronized void saveUser(User user) throws AuthenticationCredentialsException {


               logger.info(Logger.SECURITY_SUCCESS, "Saving users into database", null);


               Connection conn = null;

               try
               {
                   conn = Database.getConnection();
                   String query = "insert into user (accountid,login,hashedpasswd,roles,unblocked,enabled,oldpasswdhashes,lasthostadd,lastpasschgtamp,lastlogintamp,lastfaillogintamp,expirationtamp,faillogincount) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";

                    PreparedStatement p1=conn.prepareStatement(query);
                    p1.setLong(1,user.getAccountId());
                    p1.setString(2,user.getAccountName());
                    p1.setString(3,getHashedPassword(user));
                    p1.setString(4,dump(user.getRoles()));
                    if(user.isLocked()){
                    p1.setString(5,"locked");
                    }
                    else {
                    p1.setString(5,"unlocked");
                    }
                    if(user.isEnabled()){
                    p1.setString(6,"enabled");
                    }
                    else {
                    p1.setString(6,"disabled");
                    }
                    p1.setString(7,dump(getOldPasswordHashes(user)));
                    if(user.getLastHostAddress().equals("unknown"))
                    {
                            p1.setString(8,null);
                    }
                   else
                    {

                        p1.setString(8,user.getLastHostAddress());
                    }
                    p1.setLong(9,user.getLastPasswordChangeTime().getTime());
                    p1.setLong(10,user.getLastLoginTime().getTime());
                    p1.setLong(11,user.getLastFailedLoginTime().getTime());
                    p1.setLong(12,user.getExpirationTime().getTime());
                    p1.setInt(13,user.getFailedLoginCount());

                   int  result = p1.executeUpdate();

                   this.lastModified = System.currentTimeMillis();
                   lastChecked = lastModified;
                   logger.info(Logger.SECURITY_SUCCESS, "User saved in database " , null);
               }
               catch (SQLException e)
               {
                   logger.error(Logger.SECURITY_FAILURE,"Unable to save user throwing SQL Exception as System Error : ",e);

               }
               catch(Exception e)
               {
                   logger.error(Logger.SECURITY_FAILURE,"Unable to save user throwing  Exception as System Error : ",e);
               }
               finally
               {
                   Database.closeConnection(conn);
               }


           }

            /**
                *
                * @param user
                * @throws AuthenticationCredentialsException
                */
               public synchronized void updateUser(User user) throws AuthenticationCredentialsException {


                   logger.info(Logger.SECURITY_SUCCESS, "Saving users into database", null);


                   Connection conn = null;

                   try
                   {
                       conn = Database.getConnection();
                       String query = "update user set hashedpasswd=?,roles=?,unblocked=?,enabled=?,oldpasswdhashes=?,lasthostadd=?,lastpasschgtamp=?,lastlogintamp=?,lastfaillogintamp=?,expirationtamp=?,faillogincount=? where accountid=?";

                        PreparedStatement p1=conn.prepareStatement(query);

                        p1.setString(1,getHashedPassword(user));
                        p1.setString(2,dump(user.getRoles()));
                        if(user.isLocked()){
                        p1.setString(3,"locked");
                        }
                        else {
                        p1.setString(3,"unlocked");
                        }
                        if(user.isEnabled()){
                        p1.setString(4,"enabled");
                        }
                        else {
                        p1.setString(4,"disabled");
                        }
                        p1.setString(5,dump(getOldPasswordHashes(user)));
                       if(user.getLastHostAddress().equals("unknown"))
                       {
                             p1.setString(6,null);
                       }
                       else
                       {

                           p1.setString(6,user.getLastHostAddress());
                       }
                        p1.setLong(7,user.getLastPasswordChangeTime().getTime());
                        p1.setLong(8,user.getLastLoginTime().getTime());
                        p1.setLong(9,user.getLastFailedLoginTime().getTime());
                        p1.setLong(10,user.getExpirationTime().getTime());
                        p1.setInt(11,user.getFailedLoginCount());
                        p1.setLong(12,user.getAccountId());
                       int  result = p1.executeUpdate();

                       this.lastModified = System.currentTimeMillis();

                       loadUsersIfNecessary();

                       logger.info(Logger.SECURITY_SUCCESS, "User saved in database " , null);
                       logger.info(Logger.SECURITY_SUCCESS, "Query for User saved in database " + p1);
                   }
                   catch (SQLException e)
                   {
                       logger.error(Logger.SECURITY_FAILURE,"Unable to save user throwing SQL Exception as System Error : ",e);

                   }
                   catch(Exception e)
                   {
                       logger.error(Logger.SECURITY_FAILURE,"Unable to save user throwing  Exception as System Error : ",e);
                   }
                   finally
                   {
                       Database.closeConnection(conn);
                   }


               }



           /**
            *
            * @param user
            * @throws AuthenticationCredentialsException
            */
           public synchronized void removeUsers(User user) throws AuthenticationCredentialsException {


               logger.info(Logger.SECURITY_SUCCESS, "Removing user from database", null);


               Connection conn = null;

               try
               {
                       conn = Database.getConnection();
                       String query = "delete from user where accountId = ?";

                       PreparedStatement p2=conn.prepareStatement(query);
                       p2.setLong(1,user.getAccountId());
                       int result = p2.executeUpdate();

                   this.lastModified = System.currentTimeMillis();
                   lastChecked = lastModified;
                   logger.info(Logger.SECURITY_SUCCESS, "User removed from database", null);
               }
               catch (SQLException e)
               {
                   logger.error(Logger.SECURITY_FAILURE,"Unable to save user throwing SQL Exception as System Error : ",e);

               }
               catch(Exception e)
               {
                   logger.error(Logger.SECURITY_FAILURE,"Unable to save user throwing  Exception as System Error : ",e);
               }
               finally
               {
                   Database.closeConnection(conn);
               }


           }


           /**
            * The format is:
            * accountId | accountName | password | roles (comma separated) | unlocked | enabled | old password hashes (comma separated) |
            * last host address | last password change time | last long time | last failed login time | expiration time | failed login count
            *
            * @param user the User to save
            * @return a line containing properly formatted information to save regarding the user
            */
           private String format(ResultSet user)throws SQLException {
               StringBuilder sb = new StringBuilder();


               sb.append(user.getString("accountid"));
               sb.append(" | ");
               sb.append(user.getString("login"));
               sb.append(" | ");
               sb.append(user.getString("hashedpasswd"));
               sb.append(" | ");
               sb.append(user.getString("roles"));
               sb.append(" | ");
               sb.append(user.getString("unblocked"));
               sb.append(" | ");
               sb.append(user.getString("enabled"));
               sb.append(" | ");
               sb.append(user.getString("oldpasswdhashes"));
               sb.append(" | ");
               sb.append(user.getString("lasthostadd"));
               sb.append(" | ");
               sb.append(user.getLong("lastpasschgtamp"));
               sb.append(" | ");
               sb.append(user.getLong("lastlogintamp"));
               sb.append(" | ");
               sb.append(user.getLong("lastfaillogintamp"));
               sb.append(" | ");
               sb.append(user.getLong("expirationtamp"));
               sb.append(" | ");
               sb.append(user.getLong("faillogincount"));

               return sb.toString();
           }

           /**
            * Dump a collection as a comma-separated list.
            *
            * @param c the collection to convert to a comma separated list
            * @return a comma separated list containing the values in c
            */
           private String dump(Collection<String> c) {
               StringBuilder sb = new StringBuilder();
               for (String s : c) {
                   sb.append(s).append(",");
               }
               if ( c.size() > 0) {
                   return sb.toString().substring(0, sb.length() - 1);
               }
               return "";

           }

           /**
            * {@inheritDoc}
            * <p/>
            * This implementation simply verifies that account names are at least 5 characters long. This helps to defeat a
            * brute force attack, however the real strength comes from the name length and complexity.
            *
            * @param newAccountName
            */
           public void verifyAccountNameStrength(String newAccountName) throws AuthenticationException {
               if (newAccountName == null) {
                   throw new AuthenticationCredentialsException("Invalid account name", "Attempt to create account with a null account name");
               }
               if (!ESAPI.validator().isValidInput("verifyAccountNameStrength", newAccountName, "Login", MAX_ACCOUNT_NAME_LENGTH, false))
               {
                   throw new AuthenticationCredentialsException("Invalid account name or email", "New account name or email is not valid: " + newAccountName);
               }
           }

            public void verifyMerchantAccountNameStrength(String newAccountName) throws AuthenticationException {
               if (newAccountName == null) {
                   throw new AuthenticationCredentialsException("Invalid account name", "Attempt to create account with a null account name");
               }
               if (!ESAPI.validator().isValidInput("verifyAccountNameStrength", newAccountName, "username", MAX_ACCOUNT_NAME_LENGTH, false))
               {
                   throw new AuthenticationCredentialsException("Invalid account name or email", "New account name or email is not valid: " + newAccountName);
               }
           }

           /**
            * {@inheritDoc}
            * <p/>
            * This implementation checks: - for any 3 character substrings of the old password - for use of a length *
            * character sets > 16 (where character sets are upper, lower, digit, and special
            * jtm - 11/16/2010 - added check to verify pw != username (fix for http://code.google.com/p/owasp-esapi-java/issues/detail?id=108)
            */
           public void verifyPasswordStrength(String oldPassword, String newPassword, User user) throws AuthenticationException {
               if (newPassword == null) {
                   throw new AuthenticationCredentialsException("Invalid password", "New password cannot be null");
               }

               // can't change to a password that contains any 3 character substring of old password
               if (oldPassword != null) {
                   int length = oldPassword.length();
                   for (int i = 0; i < length - 2; i++) {
                       String sub = oldPassword.substring(i, i + 3);
                       if (newPassword.indexOf(sub) > -1) {
                           throw new AuthenticationCredentialsException("Invalid password", "New password cannot contain pieces of old password");
                       }
                   }
               }

               // new password must have enough character sets and length
               int charsets = 0;
               for (int i = 0; i < newPassword.length(); i++) {
                   if (Arrays.binarySearch(EncoderConstants.CHAR_LOWERS, newPassword.charAt(i)) >= 0) {
                       charsets++;
                       break;
                   }
               }
               for (int i = 0; i < newPassword.length(); i++) {
                   if (Arrays.binarySearch(EncoderConstants.CHAR_UPPERS, newPassword.charAt(i)) >= 0) {
                       charsets++;
                       break;
                   }
               }
               for (int i = 0; i < newPassword.length(); i++) {
                   if (Arrays.binarySearch(EncoderConstants.CHAR_DIGITS, newPassword.charAt(i)) >= 0) {
                       charsets++;
                       break;
                   }
               }
               for (int i = 0; i < newPassword.length(); i++) {
                   if (Arrays.binarySearch(EncoderConstants.CHAR_SPECIALS, newPassword.charAt(i)) >= 0) {
                       charsets++;
                       break;
                   }
               }

               // calculate and verify password strength
               int strength = newPassword.length() * charsets;
               if (strength < 16) {
                   throw new AuthenticationCredentialsException("Invalid password", "New password is not long and complex enough");
               }

               String accountName = user.getAccountName();

               //jtm - 11/3/2010 - fix for bug http://code.google.com/p/owasp-esapi-java/issues/detail?id=108
               if (accountName.equalsIgnoreCase(newPassword)) {
                   //password can't be account name
                   throw new AuthenticationCredentialsException("Invalid password", "Password matches account name, irrespective of case");
               }
           }

        public void setLastChecked(long lastChecked)
        {
            this.lastChecked = lastChecked;
        }
}

