package apps.firstproject.components.testComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import adobe.training.core.services.SightlyServiceInterface;

import com.adobe.cq.sightly.WCMUse;

public class TestComponent extends WCMUse {

  Logger logger = LoggerFactory.getLogger(TestComponent.class);
  protected String detail;
  protected String title;
  protected String image;
  protected String photoFileReference;
  protected String[] assetsPaths;

  @Override
  public void activate() {

    SightlyServiceInterface service =
        getSlingScriptHelper().getService(SightlyServiceInterface.class);
    // Get detail from a service.
    detail = service.getDeveloperData();

    // get the title of the page and the title set in the dialog
    String currentPageTitle = getCurrentPage().getProperties().get("jcr:title", String.class);
    title = getProperties().get("title", currentPageTitle);

    // get the path of the image uploaded
    image = getProperties().get("image", "no image charged");

    // get the photo reference
    photoFileReference = getProperties().get("photoFileReference", null);

    // get the paths charged in the dialog
    assetsPaths = getProperties().get("assetsPaths", null);
  }

  public String getDetail() {
    return this.detail;
  }

  public String getTitle() {
    return this.title;
  }

  public String getImage() {
    return this.image;
  }

  public String getPhotoFileReference() {
    return this.photoFileReference;
  }

  public String[] getAssetsPaths() {
    return this.assetsPaths;
  }

}
