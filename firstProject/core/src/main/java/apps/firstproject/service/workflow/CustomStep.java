package apps.firstproject.service.workflow;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;


/**
 * Este clase consiste en un servicio que lleva por nombre "Pol Test Email Workflow Process".
 * 
 * Como se implementa? Se creó un workflow llamado "DeleteContent" el cuál consiste en
 * 3 steps.
 * step 1: step por default, donde al administrador le llega un mail al inbox.
 * step 2: step "delete node" que se encarga de borrar el nodo.
 * step 3: step "process step" que en la pestaña "process", llama al servicio desarrollado, el
 * cual es el encargado de envíar el correo al usuario que elimino el page/asset, el cual
 * le informa que fue eliminado. (valga la aclaracion, NO se utiliza el step que lleva
 * por nombre "send email").
 * 
 * Como se puede ver su funcionamiento?
 * paso 1: crear una pagina
 * paso 2: hacer click derecho en la pagina creada y seleccionar el workflow que lleva
 * por nombre "DeleteContent".
 * paso 3: en el inbox del administrador llegara un correo informandole que un usuario
 * quiere eliminar la pagina. El administrador debe hacer click derecho y darle "complete".
 * paso 4: el usuario debe ir a su correo para ver que le llego un mail infomandole que
 * efectivamente la pagina fue eliminada. 
 *  
 * https://helpx.adobe.com/experience-manager/using/creating-custom-aem-workflow-steps.html).
 */

// This is a component so it can provide or consume services.
@Component
@Service
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION,
        value = "Pol Test Email workflow process implementation."),
    @Property(name = Constants.SERVICE_VENDOR, value = "Adobe"),
    @Property(name = "process.label", value = "Pol Test Email Workflow Process")})
public class CustomStep implements WorkflowProcess {

  /** Default log. */
  protected final Logger log = LoggerFactory.getLogger(CustomStep.class);

  @Reference
  private MessageGatewayService messageGatewayService;

  @Override
  public void execute(WorkItem workitem, WorkflowSession workflowsession, MetaDataMap metadatamap)
      throws WorkflowException {

    try {

      // Ensure that the execute method is invoked.
      log.info("Here is execute method");

      // Declare a MessageGateway service.
      MessageGateway<Email> messageGateway;

      // Set up the Email message.
      Email email = new SimpleEmail();

      // Set the mail values.
      String emailToRecipients = "pablodchiotti@gmail.com";
      String emailCcRecipients = "pablodchiotti@gmail.com";

      email.addTo(emailToRecipients);
      email.addTo(emailCcRecipients);
      email.setSubject("Pol AEM Custom Step");
      email.setFrom("pablodchiotti@gmail.com");
      email.setMsg("This message si to inform you that the CQ content has been deleted");

      // Inject a MessageGateway Service and send the message.
      messageGateway = messageGatewayService.getGateway(Email.class);

      // Check the logs to see that messageGateway is not null.
      messageGateway.send((Email) email);


    } catch (Exception e) {
      e.printStackTrace();
    }


  }

}
