/*
 * Created on Mar 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package business.productsubsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
	private final static Logger Log=Logger.getLogger(ProductSubsystemFacade.class.getCanonicalName());
	final String DEFAULT_PROD_DESCRIPTION="New Product";
	CatalogTypes types;
	//Log.fine("Inside product subsystem"); to put on some method 
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
		//Stab
		//System.out.print("Product Saved");
		
	}
	/* reads quantity avail and stores in the Quantity argument */
	public void readQuantityAvailable(String prodName, Quantity quantity) throws DatabaseException {
		DbClassQuantity dbclass = new DbClassQuantity();
		dbclass.setQuantity(quantity);
		dbclass.readQuantityAvail(prodName);
				
	}
	@Override
	public List<String[]> getCatalogNames() throws DatabaseException {
		List<String[]> catNames=new ArrayList<String[]>();// TODO Auto-generated method stub
		String[] arr1={"Books"};
		String[] arr2={"Clothing"};				
				catNames.add(arr1);
		        catNames.add(arr2);
		        return catNames;
	}
	@Override
	public List<String[]> refreshCatalogNames() throws DatabaseException {
		List<String[]> catNames=new ArrayList<String[]>();// TODO Auto-generated method stub
		String[] arr1={"Books"};
		String[] arr2={"Clothing"};				
				catNames.add(arr1);
		        catNames.add(arr2);		        
		        return catNames;
	}
	@Override
	public Integer getCatalogIdFromName(String catName) throws DatabaseException {
		DbClassCatalog dbclass = new DbClassCatalog();
		return dbclass.getCatalogIdForName(catName);		
		
	}
	public List<IProductFromDb> getProductList(String catType)
			throws DatabaseException {
		DbClassProduct dbclass = new DbClassProduct();
		Integer catId = getCatalogIdFromName(catType);	
		List<IProductFromDb> list = dbclass.refreshProductList(catId);	
		return list;
		
		// this is a stub
//		IProductFromDb prod =  new Product(1, "coat", "hi", "joe", "tom", 2, "other");
//		List<IProductFromDb> list = new ArrayList<IProductFromDb>();
//		list.add(prod);
//		return list;
		/*
//		IProductFromDb product=new Product(1, "TestBook", "10","5.00","11/11/00",1,"This is test");
//		List<IProductFromDb> list=new ArrayList<IProductFromDb>();
//		list.add(product);
//		// TODO Auto-generated method stub
//		return list;// TODO Auto-generated method stub
//		//return null;*/
	}
	@Override
	public List<IProductFromDb> refreshProductList(String catType)
			throws DatabaseException {
		IProductFromDb product=new Product(1, "TestBook", "10","5.00","11/11/00",1,"This is test");
		List<IProductFromDb> list=new ArrayList<IProductFromDb>();
		list.add(product);
		// TODO Auto-generated method stub
		return list;// TODO Auto-generated method stub
		//return null;
	}
//	public static void main(String[] args) throws DatabaseException
//	{
//		IProductSubsystem sub = new ProductSubsystemFacade();
//		System.out.println(sub.getCatalogIdFromName("books"));
//		
//	}
////	@Override
	//return product ID
	public String getProductIdFromName(String prodName)throws DatabaseException {
	DbClassProduct dbclass = new DbClassProduct();
    String productId=dbclass.getProductFromId(prodName).toString();
		// TODO Auto-generated method stub
		return productId;
		
	}
	@Override
	public IProductFromDb getProduct(String prodName) throws DatabaseException {
		DbClassProduct dbclass = new DbClassProduct();
		Integer catId=dbclass.getProductFromId(prodName);
		IProductFromDb product=dbclass.readProduct(catId);
		//System.out.println(product.getDescription());
		return product;
		//stab
//		IProductFromDb prodFromDb=new Product(10, "pants", "10", "10.00","10/2/2000", 1, "Best");// TODO Auto-generated method stub
//		return prodFromDb;
	}
	@Override
	public IProductFromDb getProductFromId(String prodId)
			throws DatabaseException {
		DbClassProduct dbclass = new DbClassProduct();
		IProductFromDb product = dbclass.readProduct(Integer.parseInt(prodId));
		return product;
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
