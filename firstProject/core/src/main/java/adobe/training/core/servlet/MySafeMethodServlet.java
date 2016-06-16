package adobe.training.core.servlet;

import java.io.IOException;

import javax.jcr.Repository;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

@SlingServlet(paths = "/bin/company/repo", methods = "GET")
public class MySafeMethodServlet extends SlingSafeMethodsServlet {

  private static final long serialVersionUID = -5799688229029795724L;

  @Reference
  private Repository repository;
  
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException, ServletException {
    response.setHeader("Content-Type", "application/json");
    //response.getWriter().print("{coming: \"soon\"}");
    
    /**
     * Returns a string array holding all descriptor keys available for this implementation, 
     * both the standard descriptors defined by the string constants in this interface and 
     * any implementation-specific descriptors.
     */
    String[] keys = repository.getDescriptorKeys();
    
    JSONObject jsonObject = new JSONObject();
    
    for (int i = 0; i < keys.length; i++) {
      try {
        jsonObject.put(keys[i], repository.getDescriptor(keys[i]));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    response.getWriter().print(jsonObject.toString());
    
    
  }

}
