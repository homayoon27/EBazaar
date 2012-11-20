package business.shoppingcartsubsystem;

import java.util.List;

import business.externalinterfaces.ICartItem;
import middleware.DatabaseException;
import middleware.externalinterfaces.IDbClass;

public interface DbClassShoppingCartTest extends IDbClass{

    public List<ICartItem> getCartItems(ShoppingCart shcart) throws DatabaseException;

}
