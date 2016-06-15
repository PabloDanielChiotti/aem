package adobe.training.core.services;

import javax.jcr.Repository;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate=true, metatype=true, label="Hello Bundle")
@Service(value = RepositoryService.class)
public class RepositoryServiceImpl implements RepositoryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryServiceImpl.class);

  @Reference
  private Repository repository;

  @Override
  public String getRepositoryName() {
    return repository.getDescriptor(Repository.REP_NAME_DESC);
  }

  @Activate
  protected void activate() {
    LOGGER.info("service activated");
  }

  @Deactivate
  protected void deactivate() {
    LOGGER.info("service deactivated");
  }

}
