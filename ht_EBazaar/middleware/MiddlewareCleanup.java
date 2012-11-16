
package middleware;

import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.externalinterfaces.Cleanup;
import middleware.externalinterfaces.IDataAccessSubsystem;
import middleware.externalinterfaces.IDbClass;

public class MiddlewareCleanup implements Cleanup {
    public void cleanup() {
		//release database connections
		IDataAccessSubsystem dass = new DataAccessSubsystemFacade();
		dass.releaseConnection((IDbClass)this);
    }
}
