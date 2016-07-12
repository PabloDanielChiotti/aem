package apps.firstproject.page;

import com.adobe.cq.sightly.WCMUse;
import com.day.cq.wcm.api.Page;

public class PageTitle extends WCMUse {

  private Page page;
  private String titlePage;
  
  @Override
  public void activate() throws Exception {
    String pagePath = getProperties().get("cq:signupPage","no page");
    page = getPageManager().getPage(pagePath);
    titlePage = getResourcePage().getTitle();
  }

  public Page getPage() {
    return this.page;
  }
  
  public String getTitlePage() {
    return this.titlePage;
  }
  
}
