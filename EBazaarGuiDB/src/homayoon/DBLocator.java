// SWE E-Bazaar Project (MUM CS425)
// Author: Homayoon Taheri
//
// Date: November 2012
//

package homayoon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBLocator {
	public static boolean remoteDB=true;
	public static final String PRODUCTSDB_URL = "jdbc:mysql://localhost/ProductsDb";
	public static final String ACCOUNTSDB_URL = "jdbc:mysql://localhost/AccountsDb";
	public static final String REMOTE_PRODUCTSDB_URL = "jdbc:mysql://50.56.188.144:3306/ProductsDb";
	public static final String REMOTE_ACCOUNTSDB_URL = "jdbc:mysql://50.56.188.144:3306/AccountsDb";
	public static final String DB_USERNAME = "root";
	public static final String DB_NOPASS = "";
	public static final String DB_USERPASS = "mysqlroot";
	public static final String DB_FORNAME = "org.gjt.mm.mysql.Driver";
	
	public static void SetRemoteDB() {remoteDB=true;}
	public static void SetLocalDB() {remoteDB=false;}
	public static Connection GetProductsDbConnection () {
		return GetDbConnection ((remoteDB) ? REMOTE_PRODUCTSDB_URL:PRODUCTSDB_URL);
	}

	public static Connection GetAccountsDbConnection () {
		return GetDbConnection ((remoteDB) ? REMOTE_ACCOUNTSDB_URL:ACCOUNTSDB_URL);
	}
	
	private static Connection GetDbConnection (String DB_URL) {
		Connection con = null;
    	String dburl = DB_URL;     
		try {
			Class.forName(DB_FORNAME);
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		try { 
			if (homayoon.OSValidator.isWindows())
				con = DriverManager.getConnection(dburl
							, homayoon.DBLocator.DB_USERNAME
							, homayoon.DBLocator.DB_NOPASS);
			else // is my linux 
				con = DriverManager.getConnection(dburl
							, homayoon.DBLocator.DB_USERNAME
							, homayoon.DBLocator.DB_USERPASS);
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return con;	
	}
}
