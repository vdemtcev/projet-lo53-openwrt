import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class APListener implements ServletContextListener{
	 APListenerThread listenerAP;
	 
	 public APListener(){
		 listenerAP = new APListenerThread();
		 listenerAP.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		listenerAP.stopListener();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
