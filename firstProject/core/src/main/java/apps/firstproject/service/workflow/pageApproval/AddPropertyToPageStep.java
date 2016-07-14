package apps.firstproject.service.workflow.pageApproval;

import java.util.Collections;
import java.util.Map;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;


/**
 * Esta clase consiste en un servicio para ser utilizado en un workflow (creado de cero)
 * llamado "Page Approval Workflow". Este workflow consiste de un primer step por default
 * el cual se encarga de mandar un inbox al administrador. A continuación nos encontramos
 * con un OR.
 * Por el lado A, se tienen dos steps, el segundo step es un process step que llama a este
 * servicio "Test. Add property to the page." y que le pasa como parámetro el string
 * "approved", de esta forma el servicio le agrega a la página una property llamada "resolution"
 * con el valor "approved" y por último, el tercer step se encarga de activar la página.
 * Por el lado B, se tiene un sólo step, el cuál es un process step que llama a este servicio
 * "Test. Add property to the page." y que le pasa como parámetro el string "rejected", de esta
 * forma el servicio le agrega a la página una property llamada "resolution" con el valor "rejected".
 * 
 * como se prueba?
 * paso 1: crear una página
 * paso 2: click derecho sobra la página, selecionar "workflow" y luego seleccionar 
 * "Page Approval Workflow".
 * paso 3: ir al inbox y darle complete, allí se van a disponer de las dos opciones
 * -->opción 1: Add "approved" property to the page
 * -->opción 2: Add "rejected" property to the page
 * 
 * workItem
 * 
 * 1- WorkItem represent the workItem corresponding to current step, it will be present till this step
 * is running and not present in any other process or participant step. Using this workItem object
 * you are able to access workflow instance or metaDataMap related to only this workItem. 
 * 2- You can also access workflow metaDataMap using workItem object using
 * workItem.getWorkflow().getMetaDataMap() method it will return metaDataMap related to whole
 * workflow model. 
 * 3- If you use workItem.getMetaDataMap() then it will return only that metaDataMap
 * object which is related to this step only and not present in any other workflow step. 
 * 4- You are also able to get payload of this workflow using getWorkflowData() method of workItem.
 * 5- You can explore this API from this link- WorkflowData.
 *
 * workflowSession
 * 
 * 1- WorkflowSession represent the session related to whole workflow, using this Object you can deal
 * with all node using history node of workflow instance. You can explore this interface using given
 * link WorkflowSession.
 *
 *
 * metaDataMap
 * 
 * 1- This metaDataMap object is related to dialog properties which you select in process dialog. It
 * provides you only those properties which are under process tab i.e. this will provide you
 * information about:
 * a- What process you selected? 
 * b- Either you select Handle Advance checkbox or not?
 * c- & also provide you list of arguments.
 */

@Component
@Service
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION,
        value = "Test. This service is in charge of add the property to the page."),
    @Property(name = Constants.SERVICE_VENDOR, value = "Adobe"),
    @Property(name = "process.label", value = "Test. Add property to the page.")})
public class AddPropertyToPageStep implements WorkflowProcess {

  /** Default log. */
  protected final Logger log = LoggerFactory.getLogger(AddPropertyToPageStep.class);

  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  @Override
  public void execute(WorkItem workitem, WorkflowSession workflowsession, MetaDataMap metadatamap)
      throws WorkflowException {


    log.info("inside of the method execute");

    // Get the workflow data (the data that is being passed through for this work item).
    final WorkflowData workflowData = workitem.getWorkflowData();
    final String type = workflowData.getPayloadType();

    // Check if the payload is a path in the JCR; The other (less common) type is JCR_UUID.
    if (!StringUtils.equals(type, "JCR_PATH")) {
      return;
    }

    // Get the path to the JCR resource form the payload.
    final String path = workflowData.getPayload().toString();

    // Do work on the Payload; Remember to use Sling APIs as much as possible.
    ResourceResolver resourceResolver = null;

    try {
      // Get the ResourceResolver from workflow session.
      Session session = workflowsession.adaptTo(Session.class);
      resourceResolver =
          resourceResolverFactory.getResourceResolver(Collections.<String, Object>singletonMap(
              JcrResourceConstants.AUTHENTICATION_INFO_SESSION, session));

      // Get the resolution from the args of the step.
      String resolution = metadatamap.get("PROCESS_ARGS", String.class);

      // Get the resource the payload points to; Keep in mind the payload can e any resource
      // including a AEM WF Package which must be processes specially.
      Resource resource = resourceResolver.getResource(path);
      Resource metadataRes = resourceResolver.getResource(resource, "jcr:content");
      ModifiableValueMap map = metadataRes.adaptTo(ModifiableValueMap.class);
      map.put("resolution", resolution);
      resourceResolver.commit();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
