/*
 * Created on Mar 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package business.productsubsystem;

import java.util.ArrayList;
import java.util.List;

import business.DbClassQuantity;
import business.Quantity;
import business.RuleException;
import business.rulesbeans.QuantityBean;
import business.rulesubsystem.RulesSubsystemFacade;
import business.util.*;
import business.externalinterfaces.IProductFromDb;
import business.externalinterfaces.IProductFromGui;
import business.externalinterfaces.IProductSubsystem;
import business.externalinterfaces.IRules;
import business.externalinterfaces.IRulesSubsystem;
import middleware.DatabaseException;
import middleware.EBazaarException;
import middleware.dataaccess.DataAccessUtil;

public class ProductSubsystemFacade implements IProductSubsystem {
	final String DEFAULT_PROD_DESCRIPTION="New Product";
	CatalogTypes types;
	
    public TwoKeyHashMap<Integer, String, IProductFromDb> getProductTable() throws DatabaseException {
        DbClassProduct dbClass = new DbClassProduct();
        return dbClass.readProductTable();
        
    }
	public TwoKeyHashMap<Integer, String, IProductFromDb> refreshProductTable() throws DatabaseException {
		DbClassProduct dbClass = new DbClassProduct();
        return dbClass.refreshProductTable();		
	}
	
	
	public void saveNewProduct(IProductFromGui product, String catalogType) throws DatabaseException {
		//get catalogid
		Integer catalogid = 1;//getCatalogIdFromType(catalogType); 
				//invent description
		String description = DEFAULT_PROD_DESCRIPTION;
		DbClassProduct dbclass = new DbClassProduct();
		dbclass.saveNewProduct(product, catalogid,description);
		
	}
	/* reads quantity avail and stores in the Quantity argument */
	public void readQuantityAvailable(String prodName, Quantity quantity) throws DatabaseException {
		DbClassQuantity dbclass = new DbClassQuantity();
		dbclass.setQuantity(quantity);
		dbclass.readQuantityAvail(prodName);
				
	}
	@Override
	public List<String[]> getCatalogNames() throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<String[]> refreshCatalogNames() throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<IProductFromDb> getProductList(String catType)
			throws DatabaseException {
		
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<IProductFromDb> refreshProductList(String catType)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getProductIdFromName(String prodName)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IProductFromDb getProduct(String prodName) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IProductFromDb getProductFromId(String prodId)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void saveNewCatalogName(String name) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public IProductFromGui createProduct(String name, String date,
			String numAvail, String unitPrice) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void runQuantityRules(business.externalinterfaces.Quantity quantity)
			throws RuleException, EBazaarException {
		// TODO Auto-generated method stub
		
	}
	

	

}
