package business.shoppingcartsubsystem;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import middleware.DatabaseException;
import middleware.EBazaarException;
import business.RuleException;
import business.externalinterfaces.IAddress;
import business.externalinterfaces.ICartItem;
import business.externalinterfaces.ICreditCard;
import business.externalinterfaces.ICustomerProfile;
import business.externalinterfaces.IRules;
import business.externalinterfaces.IShoppingCart;
import business.externalinterfaces.IShoppingCartSubsystem;

public class ShoppingCartSubsystemFacade implements IShoppingCartSubsystem {
	ShoppingCart liveCart;
	ShoppingCart savedCart;
	Integer shopCartId;
	ICustomerProfile customerProfile;
	Logger log = Logger.getLogger(this.getClass().getPackage().getName());

	// interface methods
	/*
	 * homayoon finalized
	 */
	public void setCustomerProfile(ICustomerProfile customerProfile) {
		this.customerProfile = customerProfile;
	}

	/*
	 * homayoon finalized
	 */
	public void retrieveSavedCart() throws DatabaseException {

		Integer val = getShoppingCartId();
		if (val != null) {
			log.info("Customer saved cart id: " + shopCartId);
			shopCartId = val;
			List<ICartItem> items = getCartItems(shopCartId);
			log.info("list of items: " + items);
			savedCart = new ShoppingCart(items);
		} else {
			savedCart = new ShoppingCart();
		}
		
		if (liveCart != null) {
			if (JOptionPane.showConfirmDialog(null,
					"There is a Saved Shopping Cart. Do you want to use it as live Cart?", "Choose cart"
					, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION )
				makeSavedCartLive();
		}
		else
			makeSavedCartLive();

	}

	// supporting methods

	Integer getShoppingCartId() throws DatabaseException {
		if (customerProfile != null) {
			DbClassShoppingCart dbshop = new DbClassShoppingCart();
			return dbshop.getShoppingCartId(customerProfile);
		}
		return 0;
	}

	List<ICartItem> getCartItems(Integer shopCartId) throws DatabaseException {
		DbClassShoppingCart dbClass = new DbClassShoppingCart();
		return dbClass.getSavedCartItems(shopCartId);
	}

	// make it a singleton
	private static ShoppingCartSubsystemFacade instance;

	public static IShoppingCartSubsystem getInstance() {
		if (instance == null) {
			instance = new ShoppingCartSubsystemFacade();
		}
		return instance;
	}

	/**
	 * Private constructor to make the facade a singleton
	 * 
	 */
	private ShoppingCartSubsystemFacade() {	}

	/*
	 * homayoon @Nov.17
	 */
	public void addCartItem(String itemName, String quantity, String totalPrice)
			throws DatabaseException {
		addCartItem(itemName, quantity, totalPrice, -1);
	}
	public void addCartItem(String itemName, String quantity,
			String totalPrice, Integer pos) throws DatabaseException {
		// if a saved cart has been retrieved, it will be the live cart, unless
		// user has already added items to a new cart
		if (liveCart == null)
			liveCart = new ShoppingCart(new LinkedList<ICartItem>());
		
		CartItem item = new CartItem(itemName, quantity, totalPrice);
		if (pos == -1)
			liveCart.addItem(item);
		else
			liveCart.insertItem(pos, item);
	}

	public boolean deleteCartItem(int pos) {
		return liveCart.deleteCartItem(pos);
	}

	public void clearLiveCart() {
		liveCart.clearCart();
	}

	public boolean deleteCartItem(String itemName) {
		return liveCart.deleteCartItem(itemName);
	}

	public List<ICartItem> getLiveCartItems() {
		if (liveCart == null || liveCart.getCartItems() == null) {
			return new LinkedList<ICartItem>();
		} else {
			return liveCart.getCartItems();
		}
	}

	public void setShippingAddress(IAddress addr) {
		// liveCart should be non-null
		liveCart.setShipAddress(addr);
	}

	public void setBillingAddress(IAddress addr) {
		liveCart.setBillAddress(addr);
	}

	public void setPaymentInfo(ICreditCard cc) {
		liveCart.setPaymentInfo(cc);
	}

	public IShoppingCart getLiveCart() {
		return liveCart;
	}

	public void makeSavedCartLive() {
		liveCart = savedCart;
	}

	public void saveLiveCart() {
		//IMPLEMENTED 
		//makeSavedCartLive();
	}

	public void runShoppingCartRules() throws RuleException, EBazaarException {
		if (liveCart == null)
			liveCart = new ShoppingCart();
		IRules transferObject = new RulesShoppingCart(liveCart);
		transferObject.runRules();
	}
}
