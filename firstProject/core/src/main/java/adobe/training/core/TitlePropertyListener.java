package adobe.training.core;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TitlePropertyListener implements EventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(TitlePropertyListener.class);

  @Reference
  private SlingRepository repository;

  private Session session;

  private ObservationManager observationManager;


  protected void activate(ComponentContext context) throws Exception {

    session = repository.loginAdministrative(null);
    observationManager = session.getWorkspace().getObservationManager();
    // the last parameter (noLocal) prevents changes done by this session to be sent to the listener
    // (which would result in an endless loop in this case).
    // you could also register for Events sent by specific node types or at specific paths only.
    // (see Event class on the JCR API).
    observationManager.addEventListener(this, Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED, "/",
        true, null, null, true);
    LOGGER.info("******* added JCR event listener");
  }

  protected void deactivate(ComponentContext componentContext) {
    try {
      if (observationManager != null) {
        observationManager.removeEventListener(this);
        LOGGER.info("******* removed JCR event listener");
      }
    } catch (RepositoryException re) {
      LOGGER.error("******* error removing the JCR event listener", re);
    } finally {
      if (session != null) {
        session.logout();
        session = null;
      }
    }
  }

  @Override
  public void onEvent(EventIterator it) {
    while (it.hasNext()) {
      Event event = it.nextEvent();
      try {
        LOGGER.info("******* new property event: {}", event.getPath());
        Property changedProperty = session.getProperty(event.getPath());
        
        if (changedProperty.getName().equalsIgnoreCase("jcr:title") && !changedProperty.getString().endsWith("!")) {
          changedProperty.setValue(changedProperty.getString());
          //changedProperty.setValue(changedProperty.getString() + "!");
          session.save();
        }      
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
      
    }


  }

}
