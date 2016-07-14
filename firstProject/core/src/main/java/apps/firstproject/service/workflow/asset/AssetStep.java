package apps.firstproject.service.workflow.asset;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.process.AbstractAssetWorkflowProcess;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.metadata.MetaDataMap;

/**
 * Esta clase extiende de AbstractAssetWorkflowProcess. y el execute tiene los
 * parametros que son del paquete com.day.cq.workflow.exec. Este paquete es viejo
 * y esta siendo reemplazado por com.adobe.granite.workflow.exec. 
 * La clase CustomStep.java sería la forma de como hacerlo con el nuevo paquete...
 * 
 * Como se implementa? En el workflow llamado "DAM Update Asset" se agrego al final
 * un process step que llema a este servicio "Add Asset Metadata" el cual se encarga
 * de agregarle una property a la metadata de una imagen cuando la misma es cargarda
 * en el DAM.
 * 
 */
// This is a component so it can provide or consume services.
@Component
@Service
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "Add metadata to the new asset."),
    @Property(name = Constants.SERVICE_VENDOR, value = "Adobe"),
    @Property(name = "process.label", value = "Add Asset Metadata")})
public class AssetStep extends AbstractAssetWorkflowProcess {

  /** Default log. */
  protected final Logger log = LoggerFactory.getLogger(AssetStep.class);

  @Reference
  private ResourceResolverFactory resolverFactory;

  ResourceResolver resourceResolver = null;

  @Override
  public void execute(WorkItem workitem, WorkflowSession workflowsession, MetaDataMap metadatamap)
      throws WorkflowException {
    
    try {
      Session session = workflowsession.getSession();
      Asset asset = getAssetFromPayload(workitem, session);
      if(asset != null) {
        String resourcePath = asset.getPath();
        Resource resource = asset.adaptTo(Resource.class);
        ResourceResolver resolver = resource.getResourceResolver();
        Resource metadataRes = resolver.getResource(resource, "jcr:content/metadata");
        ModifiableValueMap map = metadataRes.adaptTo(ModifiableValueMap.class);
        
        // Set metadata.
        map.put("dc:samplemetadata", "sample metadata");
        resolver.commit(); 
      }
    } catch(Exception e) {
      e.printStackTrace();
    }

    
  }

}
