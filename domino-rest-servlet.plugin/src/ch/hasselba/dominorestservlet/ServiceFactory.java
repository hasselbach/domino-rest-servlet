package ch.hasselba.dominorestservlet;



public class ServiceFactory extends ch.hasselba.service.ServiceFactory {
	private static ServiceFactory instance;

	// Singleton
	static {
		synchronized (ServiceFactory.class) {
			if (instance == null) {
				instance = new ServiceFactory();
				serviceLocator = new ServiceLocator();
			}
		}
	}
	
	static public ServiceFactory getInstance() {
		return instance;
	}
}
