package business.productsubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;


import middleware.DatabaseException;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.externalinterfaces.IDataAccessSubsystem;
import middleware.externalinterfaces.IDbClass;
import middleware.externalinterfaces.DbConfigKey;

/**
 * @author pcorazza
 * <p>
 * Class Description: 
 */
public class DbClassCatalogTypes implements IDbClass {

   
	private String query;
    private String queryType;
    final String GET_TYPES = "GetTypes";
   // final String READ = "Read";
   // private CatalogTypes types;
    CatalogTypes types=new CatalogTypes();
    public CatalogTypes getCatalogTypes() throws DatabaseException {
    //IMPLEMENT
    	queryType=GET_TYPES;
    	IDataAccessSubsystem daccess = new DataAccessSubsystemFacade();
    	daccess.atomicRead(this);
        return types ;
    	//return types.getInstance();
    }
    
    public void buildQuery() {
        if(queryType.equals(GET_TYPES)){//||queryType.equals(READ)){
            buildGetTypesQuery();
        }
    }

    void buildGetTypesQuery() {
        query = "SELECT * FROM CatalogType";       
    }
    /**
     * This is activated when getting all catalog types.
     */
    public void populateEntity(ResultSet resultSet) throws DatabaseException {
        types = new CatalogTypes();
        try {
			while(resultSet.next()){
				String name = resultSet.getString("catalogname");
				Integer id=resultSet.getInt("catalogid");			
				types.addCatalog(id, name);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }  
        //IMPLEMENT
        
    
    public String getDbUrl() {
    	DbConfigProperties props = new DbConfigProperties();	
    	return props.getProperty(DbConfigKey.PRODUCT_DB_URL.getVal());
    }

    public String getQuery() {

        return query;
    }

}
