package adobe.training.core.services;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service
public class SightlyService implements SightlyServiceInterface {

  Logger logger = LoggerFactory.getLogger(SightlyService.class);

  @Override
  public String getDeveloperName() {
    return "John";
  }

  @Override
  public String getDeveloperProfile() {
    return "AEM Developer";
  }

  @Override
  public String getDeveloperSkills() {
    return "JAVA, OSGI, HTML, JS";
  }

  @Override
  public String getDeveloperData() {
    String name = this.getDeveloperName();
    String profile = this.getDeveloperProfile();
    String skills = this.getDeveloperSkills();
    return name + " is a " + profile + ", He is expert in skills like " + skills;
  }

}
