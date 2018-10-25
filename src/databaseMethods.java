/* import java.sql.*; */
/*
 * Create Database public class databaseMethods { // JDBC driver name and
 * database URL static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
 * static final String DB_URL = "jdbc:mysql://localhost/";
 * 
 * // Database credentials static final String USER = "root"; static final
 * String PASS = "password";
 * 
 * public static void main(String[] args) { Connection conn = null; Statement
 * stmt = null; try { // STEP 2: Register JDBC driver
 * Class.forName("com.mysql.jdbc.Driver");
 * 
 * // STEP 3: Open a connection System.out.println("Connecting to database...");
 * conn = DriverManager.getConnection(DB_URL, USER, PASS);
 * 
 * // STEP 4: Execute a query System.out.println("Creating database..."); stmt =
 * conn.createStatement();
 * 
 * String sql = "CREATE DATABASE SDProject"; stmt.executeUpdate(sql);
 * System.out.println("Database created successfully..."); } catch (SQLException
 * se) { se.printStackTrace(); } catch (Exception e) { e.printStackTrace(); }
 * finally { // finally block used to close resources try { if (stmt != null)
 * stmt.close(); } catch (SQLException se2) { try { if (conn != null)
 * conn.close(); } catch (SQLException se) { se.printStackTrace(); } }
 * System.out.println("Goodbye!"); } } }
 */

/*
 * Select Database public class databaseMethods { // JDBC driver name and
 * database URL static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
 * static final String DB_URL = "jdbc:mysql://localhost/SDProject";
 * 
 * // Database credentials static final String USER = "root"; static final
 * String PASS = "password";
 * 
 * public static void main(String[] args) { Connection conn = null; try { //
 * STEP 2: Register JDBC driver Class.forName("com.mysql.jdbc.Driver");
 * 
 * // STEP 3: Open a connection
 * System.out.println("Connecting to a selected database..."); conn =
 * DriverManager.getConnection(DB_URL, USER, PASS);
 * System.out.println("Connected database successfully..."); } catch
 * (SQLException se) { // Handle errors for JDBC se.printStackTrace(); } catch
 * (Exception e) { // Handle errors for Class.forName e.printStackTrace(); }
 * finally { // finally block used to close resources try { if (conn != null)
 * conn.close(); } catch (SQLException se) { se.printStackTrace(); } // end
 * finally try } // end try System.out.println("Goodbye!"); }// end main }
 */
/*
 * Create Table public class databaseMethods { // JDBC driver name and database
 * URL static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; static final
 * String DB_URL = "jdbc:mysql://localhost/SDProject";
 * 
 * // Database credentials static final String USER = "root"; static final
 * String PASS = "password";
 * 
 * public static void main(String[] args) { Connection conn = null; Statement
 * stmt = null; try { // STEP 2: Register JDBC driver
 * Class.forName("com.mysql.jdbc.Driver");
 * 
 * // STEP 3: Open a connection
 * System.out.println("Connecting to a selected database..."); conn =
 * DriverManager.getConnection(DB_URL, USER, PASS);
 * System.out.println("Connected database successfully...");
 * 
 * // STEP 4: Execute a query
 * System.out.println("Creating table in given database..."); stmt =
 * conn.createStatement();
 * 
 * String sql =
 * "CREATE TABLE user_fileid (user_username varchar(512),fileid_fileid int,PRIMARY KEY(user_username,fileid_fileid));"
 * ;
 * 
 * stmt.executeUpdate(sql);
 * System.out.println("Created table in given database..."); } catch
 * (SQLException se) { // Handle errors for JDBC se.printStackTrace(); } catch
 * (Exception e) { // Handle errors for Class.forName e.printStackTrace(); }
 * finally { // finally block used to close resources try { if (stmt != null)
 * conn.close(); } catch (SQLException se) { } // do nothing try { if (conn !=
 * null) conn.close(); } catch (SQLException se) { se.printStackTrace(); } //
 * end finally try } // end try System.out.println("Goodbye!"); } }
 */
