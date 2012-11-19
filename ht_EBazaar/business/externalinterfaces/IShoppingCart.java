package business.externalinterfaces;

import java.util.List;

import middleware.DatabaseException;


public interface IShoppingCart {
    IAddress getShippingAddress();
    IAddress getBillingAddress();
    ICreditCard getPaymentInfo();
    List<ICartItem> getCartItems();
    double getTotalPrice();
    boolean deleteCartItem(String name);
    boolean isEmpty();
    
    /*
     * homayoon @Nov.17
     */
    String getCartId();
}
