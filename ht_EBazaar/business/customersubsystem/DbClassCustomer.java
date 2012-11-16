package business.customersubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import middleware.DatabaseException;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.externalinterfaces.DbConfigKey;
import middleware.externalinterfaces.IDataAccessSubsystem;
import middleware.externalinterfaces.IDbClass;

public class DbClassCustomer implements IDbClass{
	private static final Logger LOG = Logger.getLogger(DbClassAddress.class.getPackage().getName());
	private IDataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();
    private CustomerProfile Profile;
    private String queryType;
    private String query;
    private int custID;
    
    private final String SAVE = "Save";
    private final String READ = "Read";
    
 // column names
 	private final String FNAME = "fname";
 	private final String LNAME = "lname";
 	private final String ID = "custid";
 	
	@Override
	public void buildQuery() throws DatabaseException {
		if (queryType.equals(SAVE)) {
			buildSaveNewProfileQuery();
		} else if (queryType.equals(READ)) {
			buildReadProfileQuery();
		}
	}

	void buildReadProfileQuery() {
		query = "SELECT fname, lname, custid "
				+ "FROM Customer "
				+ "WHERE custid = "
				+ custID;
		
	}
	
	void readProfile(int id)
			throws DatabaseException {
		custID = id;
		queryType = READ;
		dataAccessSS.createConnection(this);
		dataAccessSS.read();	
	}
//IMPLEMENT to create new customers, suggested by Daniel.
	void buildSaveNewProfileQuery() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void populateEntity(ResultSet rs) throws DatabaseException {
		if(queryType.equals(READ))
            populateProfile(rs);
		
	}
	
	void populateProfile(ResultSet rs) throws DatabaseException {
		try {
			if (rs.next()) {
				Profile = new CustomerProfile(Integer.parseInt(rs.getString(ID)),rs.getString(FNAME)
						,rs.getString(LNAME));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	@Override
	public String getDbUrl() {
		DbConfigProperties props = new DbConfigProperties();	
    	return props.getProperty(DbConfigKey.ACCOUNT_DB_URL.getVal());
	}

	@Override
	public String getQuery() {
		return query;
	}
	
	public CustomerProfile getProfile(){
		return this.Profile;
	}
}
	