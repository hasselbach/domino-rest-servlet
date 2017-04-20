package ch.hasselba.dominorestservlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppServletContextListener
               implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        ServiceFactory.getInstance().getServiceLocator().getNotesThreadPool().shutDown();
    }

	@Override
	public void contextInitialized(ServletContextEvent event) {
				
	}

}
