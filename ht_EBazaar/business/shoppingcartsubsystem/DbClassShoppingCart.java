
package business.shoppingcartsubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List; 
import java.util.logging.Logger;

import static business.util.StringParse.*;

import business.*;
import business.externalinterfaces.IAddress;
import business.externalinterfaces.ICartItem;
import business.externalinterfaces.ICreditCard;
import business.externalinterfaces.ICustomerProfile;
import middleware.DatabaseException;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.externalinterfaces.IDataAccessSubsystem;
import middleware.externalinterfaces.IDbClass;
import middleware.externalinterfaces.DbConfigKey;


public class DbClassShoppingCart implements IDbClass {
	private static final Logger LOG = Logger.getLogger(DbClassShoppingCart.class
			.getPackage().getName());
	private IDataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();
    IDataAccessSubsystem dataAccess;
    ShoppingCart cart;
    List<ICartItem> cartItemsList;
    ICartItem curItem; // homayoon @Nov.17
    ICustomerProfile custProfile;
    Integer cartId;
    
    String query;
    final String GET_ID="GetId";
    final String GET_SAVED_ITEMS="GetSavedItems";
    final String SAVE_CART="SaveCart";
    final String SAVE_CART_ITEM="SaveCartItems";
    final String DELETE_CART="DeleteCart";
    final String DELETE_CART_ITEM="DeleteSavedCartItems";
    String queryType;
    
    /*
     * homayoon @Nov.17
     */
    public void buildQuery() {
        if(queryType.equals(GET_ID)){
            buildGetIdQuery();
        }
        else if(queryType.equals(GET_SAVED_ITEMS)){
            buildGetSavedItemsQuery();
        }
        else if(queryType.equals(SAVE_CART)){
            buildSaveCartQuery();
        }
        else if(queryType.equals(SAVE_CART_ITEM)){
            buildSaveCartItemQuery();
        }
        else if(queryType.equals(DELETE_CART)){
            buildDeleteCartQuery();
        }
        else if(queryType.equals(DELETE_CART_ITEM)){
            buildDeleteCartItemQuery();
        }
        
        if (queryType != null)
        	LOG.fine(query);
    }

    private void buildDeleteCartQuery(){
        query = "Delete "+
                "FROM 'AccountsDb'.'ShopCartTbl' "+
                "WHERE shopcustid = "+cart.getCartId() 
                ;
    }

    private void buildDeleteCartItemQuery(){
        query = "Delete "+
                "FROM 'AccountsDb'.'ShopCartItem' "+
                "WHERE shopcartid = "+cart.getCartId() 
                ;
    }

    private void buildSaveCartQuery(){
    	IAddress shipAdd = cart.getShippingAddress();
    	IAddress billAdd = cart.getBillingAddress();
    	ICreditCard ccard = cart.getPaymentInfo();
    	
        query = "Delete "+
                "FROM 'AccountsDb'.'ShopCartTbl' "+
                "WHERE shopcustid = "+cart.getCartId() 
                +";"+
                "INSERT INTO ShopCartTbl (" 
                + "'custid'"
                + ",'shippingaddress1'"
                + ",'shippingaddress2'"
                + ",'shipstate'"
                + ",'shipcity'"
                + ",'shipzipcode'"
                + ",'billaddress1'"
                + ",'billaddress2'"
                + ",'billstate'"
                + ",'billcity'"
                + ",'billzipcode'"
                + ",'nameoncard'"
                + ",'expdate'"
                + ",'cardtype'"
                + ",'cardnum'"
                + ",'totalpriceamount'"
                + ",'totalshipmentcost'"
                + ",'totaltaxamount'"
                + ",'totalamountcharged'"
                + ") VALUES ("
                + Integer.toString(custProfile.getCustId())
                + "'" + shipAdd.getStreet1() + "'" 
                + ",'" + shipAdd.getStreet2() + "'" 
                + ",'" + shipAdd.getCity() + "'" 
                + ",'" + shipAdd.getState() + "'" 
                + ",'" + shipAdd.getZip() + "'" 
                + ",'" + billAdd.getStreet1() + "'" 
                + ",'" + billAdd.getStreet2() + "'" 
                + ",'" + billAdd.getCity() + "'" 
                + ",'" + billAdd.getState() + "'" 
                + ",'" + billAdd.getZip() + "'" 
                + ",'" + ccard.getNameOnCard() + "'" 
                + ",'" + ccard.getExpirationDate() + "'" 
                + ",'" + ccard.getCardType() + "'" 
                + ",'" + ccard.getCardNum() + "'" 
                + ", 0, 0, 0,"
                + Double.toString(cart.getTotalPrice())
                + ");"
                ;
    }
    
    private void buildSaveCartItemQuery(){
    	query = "INSERT INTO ShopCartItem (" 
                + "'shopcartid'" //integer
                + ",'productid'" // integer
                + ",'quantity'"
                + ",'totalprice'"
                + ",'shipmentcost'"
                + ",'taxamount'"
                + ") VALUES ("
                + Integer.toString(cartId)
                + ", 0, 0, 0, 0, 0"
                + ");"
                ;
    }
    
    private void buildGetIdQuery(){
        query = "SELECT shopcartid "
                + "FROM ShopCartTbl "
                + "WHERE custid = " + custProfile.getCustId();               
    }
    
    /*
     * homayoon @Nov.17
     */
    private void buildGetSavedItemsQuery() {
    	//IMPLEMENTED
        query = "SELECT * " +
        		"FROM AccountsDb.ShopCartItem " +
        		"WHERE shopcartid= " + this.cartId;
    }
    
    /*
     * homayoon @Nov.17
     */    
    public Integer getShoppingCartId(ICustomerProfile custProfile) 
    									throws DatabaseException {
        this.custProfile = custProfile;
        queryType = GET_ID;
        dataAccessSS.atomicRead(this);
        return cartId;
    }
    /*
     * homayoon @Nov.17
     */    
    private void saveShoppingCart() throws DatabaseException {
    	dataAccessSS.createConnection(this);
    	queryType = DELETE_CART;
    	dataAccessSS.delete();
    	
    	queryType = SAVE_CART;
    	dataAccessSS.save();
    	dataAccess.releaseConnection(this);
    }
    
    /*
     * homayoon @Nov.17
     */    
    public Integer saveShoppingCartItems(ICustomerProfile custProfile) throws DatabaseException {
        this.custProfile = custProfile;
        dataAccessSS.createConnection(this);
        dataAccessSS.startTransaction();
        try {
        	saveShoppingCart();
        	queryType = SAVE_CART_ITEM;
            for (ICartItem ci : cartItemsList) {
            	if (!ci.isAlreadySaved()) {
            		curItem = ci;
            		dataAccessSS.save();
            	}
            }
            dataAccessSS.commit();
         }
        finally {
        	dataAccess.releaseConnection(this);
        }
        
        return cartId;
    }
    
    /*
     * homayoon @Nov.17
     */   
    public List<ICartItem> getSavedCartItems(Integer cartId) throws DatabaseException {
    	this.cartId = cartId;
    	queryType = GET_SAVED_ITEMS;
    	dataAccessSS.atomicRead(this);
        return cartItemsList;
    }

    public void populateEntity(ResultSet resultSet) throws DatabaseException {
        if(queryType.equals(GET_ID)) {
            populateShopCartId(resultSet);
        }
        else if(queryType.equals(GET_SAVED_ITEMS)){
            populateCartItemsList(resultSet);
        }        
    }

    private void populateShopCartId(ResultSet rs){
        try {
            if(rs.next()){
                cartId = rs.getInt("shopcartid");
            }
        }
        catch(SQLException e){
            //do nothing
        }   
    }
    
    /*
     * homayoon @Nov.17
     */   
    private void populateCartItemsList(ResultSet rs) throws DatabaseException {
    	//IMPLEMENTED
    	try {
    		cartItemsList = new ArrayList<ICartItem>();
    		while (rs.next()) {
    			ICartItem ci = new 
    					CartItem(rs.getInt("shopcartid")
    							, rs.getInt("productid")
    							, rs.getInt("cartitemid")
    							, rs.getString("quantity")
    							, rs.getString("totalprice")
    							, false);
    			cartItemsList.add(ci);
    			
    			LOG.fine(ci.toString());
    		}
    	}
        catch(SQLException e){
            //do nothing
        }   
    }
 
    public String getDbUrl() {
    	DbConfigProperties props = new DbConfigProperties();	
    	return props.getProperty(DbConfigKey.ACCOUNT_DB_URL.getVal());
    }

    public String getQuery() {
        return query;
    }
}
