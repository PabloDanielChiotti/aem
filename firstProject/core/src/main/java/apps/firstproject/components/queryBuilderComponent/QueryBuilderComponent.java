package apps.firstproject.components.queryBuilderComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.json.JSONObject;

import com.adobe.cq.sightly.WCMUse;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;


public class QueryBuilderComponent extends WCMUse {

  @Reference
  private QueryBuilder builder;
  
  protected JSONObject json;
  protected List<String> resultList;
  
  @Override
  public void activate() throws Exception {

    String fulltextSearchTerm = "Geometrixx";
    
    // create query description as hash map (simplest way, same as form post)
    Map<String, String> map = new HashMap<String, String>();
   
    // create query description as hash map (simplest way, same as form post)
    map.put("path", "/content/MyFirstPageName");
    map.put("type", "cq:Page");
    map.put("tagid", "surf");
    map.put("tagid.property", "jcr:content/cq:tags");
  
    // can be done in map or with Query methods
    map.put("p.offset", "0"); // same as query.setStart(0) below
    map.put("p.limit", "20"); // same as query.setHitsPerPage(20) below
                 
    Session session = getResourceResolver().adaptTo(Session.class);
    QueryBuilder builder = getResourceResolver().adaptTo(QueryBuilder.class);
    Query query = builder.createQuery(PredicateGroup.create(map), session);
    query.setStart(0);
    query.setHitsPerPage(20);
               
    SearchResult result = query.getResult();
  
    // paging metadata
    int hitsPerPage = result.getHits().size(); // 20 (set above) or lower
    long totalMatches = result.getTotalMatches();
    long offset = result.getStartIndex();
    long numberOfPages = totalMatches / 20;    
      
    json = new JSONObject();
    json.append("hitsPerPage", hitsPerPage);
    json.append("totalMatches", totalMatches);
    json.append("offset", offset);
    json.append("numberOfPages", numberOfPages);

    int counter = 0;
    resultList = new ArrayList<String>();
    for (Hit hit : result.getHits()) {
      resultList.add(hit.getPath());
      String path = hit.getPath();
      json.append("hit"+counter, hit);
      counter++;
    }

  }
  
  public JSONObject getJson() {
    return this.json;
  }
  
  public List<String> getResultList() {
    return this.resultList;
  }
  
}
