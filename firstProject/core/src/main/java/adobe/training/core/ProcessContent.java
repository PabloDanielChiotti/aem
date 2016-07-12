package adobe.training.core;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Esta clase es creada para probar el ejercicio del capitulo 10 - Testing.
 */
public class ProcessContent {

  protected static final String CONTENT_NODENAME = "content";
  
  // does not depend on JCR
  public String stripNonLettersOrNumbers(String in) {
    if (in != null) {
      return in.replaceAll("[^\\p{L}\\p{N}]", "");
    } else {
      return null;
    }
  }
  
  // depends on JCR environment specifics
  public String getContentPath(Session session) throws RepositoryException {
    Node rootNode = session.getRootNode();
    if (rootNode.hasNode(CONTENT_NODENAME)) {
      Node contentNode = rootNode.getNode(CONTENT_NODENAME);
      return contentNode.getPath();
    } else {
      return rootNode.getPath();
    }
  }
}
