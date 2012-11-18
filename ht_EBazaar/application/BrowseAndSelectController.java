package application;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import business.DbClassQuantity;
import business.Quantity;
import business.RuleException;
import business.RulesQuantity;
import business.SessionContext;
import business.customersubsystem.CustomerSubsystemFacade;
import business.externalinterfaces.CustomerConstants;
import business.externalinterfaces.ICartItem;
import business.externalinterfaces.ICustomerSubsystem;
import business.externalinterfaces.IProductFromDb;
import business.externalinterfaces.IRules;
import business.externalinterfaces.IShoppingCart;
import business.externalinterfaces.IShoppingCartSubsystem;
import business.productsubsystem.ProductSubsystemFacade;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import middleware.DatabaseException;
import middleware.EBazaarException;
import application.gui.CartItemsWindow;
import application.gui.CatalogListWindow;
import application.gui.DefaultData;
import application.gui.EbazaarMainFrame;
import application.gui.MaintainCatalogTypes;
import application.gui.MaintainProductCatalog;
import application.gui.ProductDetailsWindow;
import application.gui.ProductListWindow;
import application.gui.QuantityWindow;
import application.gui.SelectOrderWindow;
import application.gui.WaitDialog;

public class BrowseAndSelectController implements CleanupControl {
	private static final Logger LOG = Logger
			.getLogger("BrowseAndSelectController.class.getName()");

	// homayoon @Nov.15
	// control of mainFrame
	class PurchaseOnlineActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		
			WaitDialog wd = new WaitDialog();
			wd.show("Retrieve catalog list.");
			try {
				List<String[]> catalogs = 
						(new ProductSubsystemFacade()).getCatalogNames();
				catalogListWindow = new CatalogListWindow();
				mainFrame.getDesktop().add(catalogListWindow);
				catalogListWindow.updateModel(catalogs);
				catalogListWindow.setVisible(true);
				LOG.info("Purchase online selected.");
			} catch (DatabaseException dbe) {
				String errMsg = "Database inaccessible: " + dbe.getMessage();
				JOptionPane.showMessageDialog(catalogListWindow, errMsg, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			finally {
				wd.clear();
			}
		}
	}

	class LoginListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				LoginControl loginControl = new LoginControl(mainFrame,	mainFrame);
				loginControl.startLogin();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/*
	 * homay @Nov.18
	 */
	class RetrieveCartActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (cartItemsWindow == null) {
				cartItemsWindow = new CartItemsWindow();
				mainFrame.getDesktop().add(catalogListWindow);
			}
			EbazaarMainFrame.getInstance().getDesktop().add(cartItemsWindow);
			IShoppingCartSubsystem shcss = ShoppingCartSubsystemFacade.getInstance(); 
			List<ICartItem> items = shcss.getLiveCartItems();
			List<String[]> itemList = new ArrayList<String[]>();
			System.out.println("*** Items in Shopping Cart" + items.size());
			if (items.size() == 0) {
				JOptionPane.showMessageDialog(mainFrame, "The Shopping Cart Is empty. Continue Shopping.", ":",
						JOptionPane.INFORMATION_MESSAGE);
				PurchaseOnlineActionListener shopping 
								= new PurchaseOnlineActionListener();
				shopping.actionPerformed(null);
			}
			else {
				for (ICartItem ci : items) {
					String unitPrice 
						= Double.toString(Double.valueOf(ci.getTotalprice())
										/Integer.valueOf(ci.getQuantity()));
					itemList.add(new String[]{ci.getProductName(),ci.getQuantity()
												, ci.getTotalprice(), unitPrice});
				}
				cartItemsWindow.updateModel(itemList);
				cartItemsWindow.setVisible(true);
			}
		}
	}

	// control of CatalogListWindow
	/*
	 * homayoon @Nov.15 
	 */
	class SelectCatalogListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JTable table = catalogListWindow.getTable();
			int selectedRow = table.getSelectedRow();
			if (selectedRow >= 0) {
				String type = (String) table.getValueAt(selectedRow, 0);
				try {
					List<IProductFromDb> prodList = 
							(new ProductSubsystemFacade()).getProductList(type);
					List<String[]> products = new ArrayList<String[]>();
					for (IProductFromDb prod : prodList) {
						String[] row = {prod.getProductName()};
						products.add(row);
					}
					productListWindow = new ProductListWindow(type);
					productListWindow.updateModel(products);
					catalogListWindow.setVisible(false);
					mainFrame.getDesktop().add(productListWindow);
					productListWindow.setVisible(true);
					LOG.info("Selected type: " + type + " shown.");
					
				} catch (DatabaseException dbe) {
					String errMsg = "Database inaccessible: " + dbe.getMessage();
					JOptionPane.showMessageDialog(catalogListWindow, errMsg, "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			else { // arriving here means no row was selected
				String errMsg = "Please select a row.";
				JOptionPane.showMessageDialog(catalogListWindow, errMsg,
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	class BackToMainFrameListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			mainFrame.setVisible(true);
			catalogListWindow.setVisible(false);
		}
	}

	// Control of ProductListWindow
	class SelectProductListener implements ActionListener {
		HashMap<String, String[]> readProductDetailsData() {
			DefaultData productData = DefaultData.getInstance();
			return productData.getProductDetailsData();
		}

		boolean useDefaultData;

		public SelectProductListener(boolean useDefData) {
			useDefaultData = useDefData;
		}

		/* 
		 * homayoon @Nov.15
		 */
		String[] readProductDetailsData(String prodName) {
			if (useDefaultData) {
				DefaultData productData = DefaultData.getInstance();
				return productData.getProductDetailsData(prodName);
			} 
			else {
				try {
					IProductFromDb product 
							= (new ProductSubsystemFacade()).getProduct(prodName);
					return new String[]{product.getProductName(), product.getUnitPrice()
							, product.getQuantityAvail(), product.getDescription()};
				} 
				catch (DatabaseException dbe) {
					String errMsg = "Database inaccessible: " + dbe.getMessage();
					JOptionPane.showMessageDialog(catalogListWindow, errMsg, "Error",
							JOptionPane.ERROR_MESSAGE);					
				}
				finally {					
				}
			}
			return null;
		}

		public void actionPerformed(ActionEvent evt) {
			JTable table = productListWindow.getTable();
			int selectedRow = table.getSelectedRow();
			if (selectedRow >= 0) {
				String prodName = (String) table.getValueAt(selectedRow, 0);
				String[] productParams = readProductDetailsData(prodName);
				productDetailsWindow = new ProductDetailsWindow(productParams);
				mainFrame.getDesktop().add(productDetailsWindow);
				productListWindow.setVisible(false);
				productDetailsWindow.setVisible(true);
			}
			// Value of selectedRow is -1, which means no row was selected
			else {
				String errMsg = "Please select a row.";
				JOptionPane.showMessageDialog(productListWindow, errMsg,
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	class BackToCatalogListListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			catalogListWindow.setVisible(true);
			productListWindow.setVisible(false);
		}
	}

	// ///// control ProductDetails
	class AddCartItemListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			productDetailsWindow.setVisible(false);
			quantityWindow = new QuantityWindow(false, null);
			EbazaarMainFrame.getInstance().getDesktop().add(quantityWindow);
			quantityWindow.setVisible(true);
			quantityWindow.setParentWindow(productDetailsWindow);
		}
	}
	/*
	 * homayoon @Nov.18
	 */
	class DeleteShoppingCartItemListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			
		}
	}
	class EditShoppingCartItemListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			cartItemsWindow.setVisible(false);
			quantityWindow = new QuantityWindow(false, null);
			EbazaarMainFrame.getInstance().getDesktop().add(quantityWindow);
			quantityWindow.setVisible(true);
			quantityWindow.setParentWindow(productDetailsWindow);			
		}
	}
	
	class QuantityOkListener implements ActionListener {
		QuantityOkListener() {
			System.out.println("*** Initialize QuantityOkListener ***");
		}
		public void actionPerformed(ActionEvent evt) {

			/*
			 * homayoon @Nov.16
			 */			
			//insert rules
        	//gather quantity data 
        	//   ... read quantity requested from window
        	//           --create instance of Quantity
        	//   ... read quantity avail from database
        	//           -- uses DbClassQuantity
        	//              use populateEntity of DbClassQuantity
        	//              to populate the value quantityAvail
        	//              in Quantity
        	//

			DbClassQuantity dbQty = new DbClassQuantity();
			Quantity qty = new Quantity(quantityWindow.getQuantityDesired());
			dbQty.setQuantity(qty);
			boolean err = false;
			try {
	            dbQty.readQuantityAvail(productDetailsWindow.getItem());
	            LOG.fine("Read Quantity of " + productDetailsWindow.getItem() + " From Table");
	        }
	        catch(DatabaseException dbe){
				String errMsg = "Database inaccessible: " + dbe.getMessage();
				JOptionPane.showMessageDialog(catalogListWindow, errMsg, "Error",
						JOptionPane.ERROR_MESSAGE);	
				err = true;
			}			
			IRules rules = new RulesQuantity(qty);
			try {
				rules.runRules();
			} catch (RuleException re) {
				String errMsg = "Rules dosn't match: " + re.getMessage();
				JOptionPane.showMessageDialog(quantityWindow, errMsg, "Error",
						JOptionPane.ERROR_MESSAGE);	
				err = true;
			} catch (EBazaarException ee) {
				String errMsg = "EBazaar exception: " + ee.getMessage();
				JOptionPane.showMessageDialog(quantityWindow, errMsg, "Error",
						JOptionPane.ERROR_MESSAGE);					
			}

			if (err)
				quantityWindow.setVisible(true);
			else 
			{
				WaitDialog wd = new WaitDialog();
				wd.show("update total quantity");
				
				quantityWindow.dispose();
				IShoppingCartSubsystem shcss = ShoppingCartSubsystemFacade.getInstance();
				
				// should update totalquantity in database
				int newQty = Integer.valueOf(qty.getQuantityAvailable())-Integer.valueOf(qty.getQuantityRequested());
				qty.setQuantityAvailable(Integer.toString(newQty));
				dbQty.setQuantity(qty);
				try {
		            dbQty.updateQuantityAvail(productDetailsWindow.getItem());
		            LOG.fine("Update Quantity of " + productDetailsWindow.getItem() + " into product Table");
					shcss.addCartItem(productDetailsWindow.getItem()
							, qty.getQuantityRequested()
							, Double.toString(productDetailsWindow.getPrice()));
		        }
		        catch(DatabaseException dbe){
					String errMsg = "Database inaccessible: " + dbe.getMessage();
					JOptionPane.showMessageDialog(catalogListWindow, errMsg, "Error",
							JOptionPane.ERROR_MESSAGE);				        
				}		
				finally {
					wd.clear();
				}
				
				RetrieveCartActionListener shopCart 
								= new RetrieveCartActionListener();
				shopCart.actionPerformed(null);
			}
		}
	}

	/*
	 * homayoon @Nov.18
	 */
	class BackToProductListListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			productDetailsWindow.setVisible(false);
			catalogListWindow.setVisible(true);
		}
	}

	// /// control CartItemsWindow

	class ContinueShoppingListener implements ActionListener {

		public void actionPerformed(ActionEvent evt) {
			cartItemsWindow.setVisible(false);

			// user has been looking at this product list
			if (productListWindow != null) {
				productListWindow.setVisible(true);
			}
			// user has just retrieved saved cart
			else {
				if (catalogListWindow != null) {
					catalogListWindow.dispose();
				}
				catalogListWindow = new CatalogListWindow();
				EbazaarMainFrame.getInstance().getDesktop()
						.add(catalogListWindow);
				catalogListWindow.setVisible(true);
			}
		}
	}

	/*
	 * homayoon @Nov.16
	 */			
	// implement
	// here's the logic:
	// require login if not logged in
	// if current live cart does not have a cartid then it's new;
	// save cart level data, read the auto-generated
	// cart id, then loop
	// and get lineitemid's for each line, inserting
	// the relevant cartid, and save each line
	// If current cart does have an id, we just save
	// cart items that are not flagged as "hasBeenSaved"
	// (a boolean in that class)
	// no need to save at the cart level, just save the
	// not-so-far-saved cart items
	// postcondition: the live cart has a cartid
	// and all cart items are flagged as "hasBeenSaved"
	class SaveCartListener implements ActionListener , Controller {
		/*
		 * this method is called when LoginControl needs this class to load
		 * shoppingcart
		 */
		public void doUpdate() {}

		public void actionPerformed(ActionEvent evt) {
			IShoppingCartSubsystem shcss = ShoppingCartSubsystemFacade.getInstance();
			SessionContext sctx = SessionContext.getInstance();
			Boolean loggedIn = (Boolean) sctx.get(CustomerConstants.LOGGED_IN);
			if (!loggedIn.booleanValue()) {
				LoginControl loginControl = new LoginControl(cartItemsWindow, mainFrame, this);
				loginControl.startLogin();
				System.out.println("Successful Login.");
			} else {
				mainFrame.getDesktop().add(maintainProductCatalog);
				maintainProductCatalog.setVisible(true);
			}
			IShoppingCart curCard = shcss.getLiveCart();
			String curtId = curCard.getCartId();
			if (curtId == null) {
				
			}
			else {
				
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
		}
	}
		
	// /////// PUBLIC INTERFACE -- for getting instances of listeners ///
	// EbazaarMainFrame
	public ActionListener getNewOnlinePurchaseListener(EbazaarMainFrame f) {
		return (new PurchaseOnlineActionListener());
	}

	public LoginListener getLoginListener(EbazaarMainFrame f) {
		return new LoginListener();
	}

	public ActionListener getRetrieveCartActionListener(EbazaarMainFrame f) {
		return (new RetrieveCartActionListener());
	}

	// CatalogListWindow
	public ActionListener getSelectCatalogListener(CatalogListWindow w) {
		return new SelectCatalogListener();
	}

	public ActionListener getBackToMainFrameListener(CatalogListWindow w) {
		return new BackToMainFrameListener();
	}

	// ProductListWindow
	public ActionListener getSelectProductListener(ProductListWindow w,
			boolean useDefData) {
		return new SelectProductListener(useDefData);
	}

	public ActionListener getBackToCatalogListListener(ProductListWindow w) {
		return new BackToCatalogListListener();
	}

	// ProductDetails Window
	public ActionListener getAddToCartListener(ProductDetailsWindow w) {
		return new AddCartItemListener();
	}

	public ActionListener getBackToProductListListener(ProductDetailsWindow w) {
		return new BackToProductListListener();
	}

	// CartItemsWindow

	public ActionListener getContinueShoppingListener(CartItemsWindow w) {
		return (new ContinueShoppingListener());
	}

	public ActionListener getSaveCartListener(CartItemsWindow w) {
		return (new SaveCartListener());
	}

	/*
	 * homayoon @Nov.18 
	 */
	public ActionListener getDeleteShoppingCartItemListener(CartItemsWindow w) {
		return new DeleteShoppingCartItemListener();
	}
	public ActionListener getEditShoppingCartItemListener(CartItemsWindow w) {
		return new EditShoppingCartItemListener();
	}

	public ActionListener getQuantityOkListener(QuantityWindow w, boolean edit,
			Integer posOfEdit) {
		return new QuantityOkListener();
	}

	// ////// PUBLIC ACCESSORS to register screens controlled by this class////

	public void setCatalogList(CatalogListWindow w) {
		catalogListWindow = w;
	}

	public void setMainFrame(EbazaarMainFrame m) {
		mainFrame = m;
	}

	public void setProductListWindow(ProductListWindow p) {
		productListWindow = p;
	}

	public void setProductDetailsWindow(ProductDetailsWindow p) {
		productDetailsWindow = p;
	}

	public void setCartItemsWindow(CartItemsWindow w) {
		cartItemsWindow = w;
	}

	public void setSelectOrderWindow(SelectOrderWindow w) {
		selectOrderWindow = w;
	}

	public void setMaintainCatalogTypes(MaintainCatalogTypes w) {
		maintainCatalogTypes = w;
	}

	public void setMaintainProductCatalog(MaintainProductCatalog w) {
		maintainProductCatalog = w;
	}

	public void setQuantityWindow(QuantityWindow w) {
		quantityWindow = w;
	}

	// private boolean edit;

	// ///// screens -- private references
	private EbazaarMainFrame mainFrame;
	private ProductListWindow productListWindow;
	private CatalogListWindow catalogListWindow;
	private ProductDetailsWindow productDetailsWindow;
	private CartItemsWindow cartItemsWindow;
	private SelectOrderWindow selectOrderWindow;
	private MaintainCatalogTypes maintainCatalogTypes;
	private MaintainProductCatalog maintainProductCatalog;
	private QuantityWindow quantityWindow;
	private Window[] allWindows = {};
	private JInternalFrame[] internalFrames = { productListWindow,
			catalogListWindow, productDetailsWindow, quantityWindow,
			cartItemsWindow, selectOrderWindow, maintainCatalogTypes,
			maintainProductCatalog };

	public void cleanUp() {
		ApplicationUtil.cleanup(allWindows);
		ApplicationUtil.cleanup(internalFrames);
	}

	// ///// make this class a singleton
	private static final BrowseAndSelectController instance = new BrowseAndSelectController();

	public static BrowseAndSelectController getInstance() {
		return instance;
	}

	private BrowseAndSelectController() {
	}

	// /////// communication with other controllers
	public void makeMainFrameVisible() {
		if (mainFrame == null) {
			mainFrame = new EbazaarMainFrame();
		}
		mainFrame.setVisible(true);
	}
}
