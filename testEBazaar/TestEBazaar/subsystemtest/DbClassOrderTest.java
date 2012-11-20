package subsystemtest;

import integrationtests.BrowseAndSelectTest;

import java.util.List;
import java.util.logging.Logger;

import junit.framework.TestCase;

import middleware.DatabaseException;
import alltests.AllTests;
import business.externalinterfaces.IProductSubsystem;
import business.externalinterfaces.ICustomerSubsystem;
import business.externalinterfaces.IOrderSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;
import business.productsubsystem.ProductSubsystemFacade;
import business.customersubsystem.CustomerSubsystemFacade;
import dbsetup.DbQueries;

public class DbClassOrderTest extends TestCase{
	static String name = "DbClassOrder Test";
	//static Logger log = Logger.getLogger(DbClassOrderTest.class.getName());
	
	static {
		AllTests.initializeProperties();
	}
	
	
	public void testCatalogListStep() {
		// Add row in CatalogType table for testing
		List<Integer> expectedIds = DbQueries.readOrderIds();
		System.out.println("Size = " + expectedIds.size());
		// Perform test
		ICustomerSubsystem css = new CustomerSubsystemFacade();
        IOrderSubsystem oss = new OrderSubsystemFacade(css.getDefaultCustomerProfile());
        //Integer custId = css.getDefaultCustomerProfile().getCustId();
		List<Integer> custIds=null;
        
		try {
			custIds =oss.getAllOrderIds();
            oss.getAllOrderIds();
		}
		catch(DatabaseException ex){
			fail("DatabaseException: " + ex.getMessage());
		}
		finally {
			assertTrue(custIds != null);
			boolean isIdsEqual = true;
			if(custIds != null){
				for(int i = 0; i<custIds.size();i++){
					if(custIds.get(i) != expectedIds.get(i)){
						isIdsEqual = false;
					}
				}
			}
			assertTrue(isIdsEqual);
			
		}
	}
}
