package adobe.training.core.services;

import java.util.Dictionary;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.OsgiUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//metatype=true, enables configuration in AEM Web Console
@Component(immediate=true, metatype=true, label="Cleanup Service")
@Service(value=Runnable.class)
@Property(name="scheduler.expression", value="*/5 * * * * ?") //Every 5 seconds
public class CleanupServiceImpl implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(CleanupServiceImpl.class);
  
  @Reference
  private SlingRepository repository;

  @Property(label="Path", description="Delete the path", value="/mypath")
  public static final String CLEANUP_PATH = "cleanupPath";
  
  private String cleanupPath;
  
  protected void activate(ComponentContext componentContext) {
    configure(componentContext.getProperties());
  }
  
  //configure will be called when the configuration changes. This is used to obtain the new
  //value for the cleanup path when it is changed.
  protected void configure(Dictionary<?,?> properties) {
    this.cleanupPath = OsgiUtil.toString(properties.get(CLEANUP_PATH), null);
    LOGGER.info("configure: cleanupPath='{}'", this.cleanupPath);
  }
  
  @Override
  public void run() {
    LOGGER.info("running now");
    Session session = null;
    try {
      session = repository.loginAdministrative(null);
      if(session.itemExists(cleanupPath)) {
        session.removeItem(cleanupPath);
        LOGGER.info("node deleted");
        session.save();
      }
    } catch (Exception e) {
      LOGGER.error("exception during cleanup", e);
    } finally {
      if (session != null) {
        session.logout();
      }
    }  
  }

  
  
}
