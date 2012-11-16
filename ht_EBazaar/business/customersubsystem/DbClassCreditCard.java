
package business.customersubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import business.externalinterfaces.ICreditCard;
import business.externalinterfaces.ICustomerProfile;
import middleware.DatabaseException;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.externalinterfaces.IDataAccessSubsystem;
import middleware.externalinterfaces.IDbClass;
import middleware.externalinterfaces.DbConfigKey;


class DbClassCreditCard implements IDbClass {
    private final String READ = "Read";
    private static final Logger LOG = Logger.getLogger(DbClassAddress.class
			.getPackage().getName());
	private IDataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();
    private ICustomerProfile custProfile;
    private ICreditCard cc;
    private CreditCard defaultCreditCard;
    //column name	
	private final String NAME = "nameoncard";
	private final String DATE = "expdate";
	private final String TYPE = "cardtype";
	private final String NUM = "cardnum";

    String query;
    private String queryType;
 
    public void buildQuery() {
        if(queryType.equals(READ)){
            buildReadQuery();
        }
    }
    
    void buildReadQuery() {
        //IMPLEMENTED
    	query = "SELECT nameoncard , expdate, cardtype, cardnum "
				+ "FROM Customer "
				+ "WHERE custid = "
				+ custProfile.getCustId();
    }
    
 
    public void populateEntity(ResultSet resultSet) throws DatabaseException {
        //IMPLEMENTED
    	if (queryType.equals(READ)) {
			populateDefaultCreditCard(resultSet);
    	}
    }


    private void populateDefaultCreditCard(ResultSet rs) throws DatabaseException {
    	//IMPLEMENTED
    	try {
			if (rs.next()) {
				defaultCreditCard = new CreditCard(rs.getString("nameoncard"),
						rs.getString("expdate"), rs.getString("cardtype"),
						rs.getString("cardnum"));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
		
	}

	public String getDbUrl() {
    	DbConfigProperties props = new DbConfigProperties();	
    	return props.getProperty(DbConfigKey.ACCOUNT_DB_URL.getVal());
 
    }

 
    public String getQuery() {
        return query;
 
    }

	public void readDefaultCreditCard(CustomerProfile customerProfile) throws DatabaseException {
		//IMPLEMENTED
				this.custProfile = customerProfile;
				queryType = READ;
				dataAccessSS.createConnection(this);
				dataAccessSS.read();		
	}

	public CreditCard getDefaultCreditCard() {
		//IMPLEMENTED
		return this.defaultCreditCard;
	}

 
}
