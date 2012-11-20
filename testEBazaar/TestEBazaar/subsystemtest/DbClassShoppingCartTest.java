/**
 * 
 */
package subsystemtest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import middleware.DatabaseException;

import org.junit.Test;

import dbsetup.DbQueries;

import business.externalinterfaces.ICartItem;
import business.externalinterfaces.ICustomerProfile;
import business.externalinterfaces.ICustomerSubsystem;
import business.externalinterfaces.IShoppingCartSubsystem;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import business.customersubsystem.CustomerSubsystemFacade;

import alltests.AllTests;

/**
 * @author homayoon
 *
 */
public class DbClassShoppingCartTest extends TestCase {
	static String name = "DbClass Shoppung Cart Test";
	
	static {
		AllTests.initializeProperties();
	}
	
	@Test
	public void test(){ // test retrieve shopping cart using Shoppingcart subsystem 

		IShoppingCartSubsystem shcss = ShoppingCartSubsystemFacade.getInstance();
		ICustomerSubsystem css = new CustomerSubsystemFacade();
		try {
			css.initializeCustomer(1);
		} catch (DatabaseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ICustomerProfile cp = css.getCustomerProfile();
		cp.setCustId(1);
		shcss.setCustomerProfile(cp);
		try {
			shcss.retrieveSavedCart();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		shcss.makeSavedCartLive();
		List<ICartItem> ci = shcss.getLiveCartItems();
		if (!ci.isEmpty()) {
			for (ICartItem c : ci) {
				System.out.println (c.toString());
			}			
		}
		
		int id = DbQueries.retrieveShoppingCard();
		System.out.println("Get Cart Id == " + Integer.parseInt(shcss.getLiveCart().getCartId()));
		System.out.println("Read Cart Id == " + id);
		
		assertTrue(id == Integer.parseInt(shcss.getLiveCart().getCartId()));

	
	}

}
