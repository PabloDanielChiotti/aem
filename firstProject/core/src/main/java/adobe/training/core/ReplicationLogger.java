package adobe.training.core;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.JobProcessor;
import org.apache.sling.jcr.resource.JcrResourceResolverFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * Aquí se crea un evento listener que escucha page replication events, y loguea 
 * los detaller cuando alguno ocurre. Algo así hay que utilizar cuando se quiere
 * mejorar otras operaciones lanzadas por eventos en el lado del servidor.
 */


@Service(value = EventHandler.class)
/**
 * The statement below is needed to ensure that the component adobe.training.core.ReplicationLogger
 * is active immediately - otherwise it would only be installer when used, and since it is supposed
 * to be listening for events, this would not work.
 */
@Component(immediate = true)
@Property(name = "event.topics", value = ReplicationAction.EVENT_TOPIC)
public class ReplicationLogger implements EventHandler, JobProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationLogger.class);

  @Reference
  private JcrResourceResolverFactory jcrResourceResolverFactory;
  
  /**
   * Existe una forma mas nueva para obtener el resource resolver.
   */
  @Override
  public boolean process(Event event) {
    LOGGER.debug("*******processing job");
    
    ReplicationAction action = ReplicationAction.fromEvent(event);
    ResourceResolver resourceResolver = null;
    
    if(action.getType().equals(ReplicationActionType.ACTIVATE)) {
      try {
        resourceResolver = jcrResourceResolverFactory.getAdministrativeResourceResolver(null);
        final PageManager pm = resourceResolver.adaptTo(PageManager.class);
        final Page page = pm.getContainingPage(action.getPath());
        if(page != null) {
          LOGGER.debug("*******activation of page {}", page.getTitle());
        }
      } catch (LoginException e) {
        e.printStackTrace();
      } finally {
        if(resourceResolver != null && resourceResolver.isLive()) {
          resourceResolver.close();
        }
      }
    }
    
    
    return true;
  }

  @Override
  public void handleEvent(Event event) {
    LOGGER.debug("*******handling event");
    process(event);
  }

}
