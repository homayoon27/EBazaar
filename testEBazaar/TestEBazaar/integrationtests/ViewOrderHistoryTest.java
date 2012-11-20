package integrationtests;

import java.util.List;
import java.util.logging.Logger;

import middleware.DatabaseException;
import business.externalinterfaces.IOrder;
import business.externalinterfaces.IOrderSubsystem;
import business.externalinterfaces.ICustomerSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;
import business.customersubsystem.CustomerSubsystemFacade;

import dbsetup.DbQueries;

import alltests.AllTests;

import junit.framework.TestCase;

public class ViewOrderHistoryTest extends TestCase {
	static String name = "View Order History Test";
	static Logger log = Logger.getLogger(ViewOrderHistoryTest.class.getName());
	static {
		AllTests.initializeProperties();
	}
	public void testOrderListStep() throws DatabaseException  {
		//Add row in CatalogType table for testing
		String[] vals = DbQueries.insertOrderRow();
		Integer expectedId = Integer.parseInt(vals[2]);
		System.out.println("Expected ID = " + expectedId);
		// Perform test
		ICustomerSubsystem css = new CustomerSubsystemFacade();
		IOrderSubsystem oss = new OrderSubsystemFacade(css.getDefaultCustomerProfile());
		System.out.println(css.getDefaultCustomerProfile().getFirstName());
		List<IOrder> orders=null;
		
		try {
			 
			orders = oss.getOrderHistory();	
			   
		}
		catch(DatabaseException ex){
			fail("DatabaseException: " + ex.getMessage());
		}
		finally {			
			assertTrue(orders != null);
			boolean orderIdFound = false;
			if(orders != null){
				for (IOrder next : orders) {
					if(next.getOrderId() == expectedId) {
						orderIdFound=true;
						System.out.println(next.getOrderId());
						break;
					}
				}
			}
			assertTrue(orderIdFound);
			// Clean up table
			DbQueries.deleteOrderRow(Integer.parseInt(vals[2]));
			
			
		}

	}
}
