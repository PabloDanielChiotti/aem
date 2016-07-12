package adobe.training.core.servlet;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;

import com.day.cq.wcm.api.PageManager;

/**
 * The servlet will perform a full text search starting with the specifies node. the result set will be a set of pages, but we will specify
 * node type nt:unstructured, because we know that the jcr:content child of each page is of type nt:unstructured.
 */
@SlingServlet(resourceTypes="geometrixx/components/homepage", selectors="search")
public class SearchServlet extends SlingSafeMethodsServlet {

  private static final long serialVersionUID = 2387691855799676842L;

  
  public final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
    response.setHeader("Content-Type", "application/json");
    JSONObject jsonObject = new JSONObject();
    JSONArray resultArray = new JSONArray();
    
    try {
      //this is the current node that is requested, in case of a page that is the jcr:content node.
      Node currentNode = request.getResource().adaptTo(Node.class);
      PageManager pageManager = (PageManager) request.getResource().getResourceResolver();
      
      //node that is the cq:Page containing the requested node
      Node queryRoot = pageManager.getContainingPage(currentNode.getPath()).adaptTo(Node.class);
      
      String queryTerm = request.getParameter("q");
      if(queryTerm != null) {
        NodeIterator searchResults = performSearch(queryRoot, queryTerm);
        while (searchResults.hasNext()) {
          resultArray.put(searchResults.nextNode().getPath());
        }
      }      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    //response.getWritter().print("SQL VERSION");
    response.getWriter().print(jsonObject.toString());
  }
  
  
  private NodeIterator performSearch(Node queryRoot, String queryTerm) throws RepositoryException {
    //JQOM Infrastructure.
    QueryObjectModelFactory qf = queryRoot.getSession().getWorkspace().getQueryManager().getQOMFactory();
    ValueFactory vf = queryRoot.getSession().getValueFactory();
    
    final String SELECTOR_NAME = "all results";
    final String SELECTOR_NT_UNSTRUCTURED = "nt:unstructured";
    
    //select all unstructured nodes
    Selector selector = qf.selector(SELECTOR_NT_UNSTRUCTURED, SELECTOR_NAME);
    
    //full text constraint
    Constraint constraint = qf.fullTextSearch(SELECTOR_NAME, null, qf.literal(vf.createValue(queryTerm)));
    
    //path constraint
    constraint = qf.and(constraint, qf.descendantNode(SELECTOR_NAME, queryRoot.getPath()));
    
    //execute the query without explicit order and columns
    QueryObjectModel query = qf.createQuery(selector, constraint, null, null);
    
    return query.execute().getNodes();
  }
  
  
}
