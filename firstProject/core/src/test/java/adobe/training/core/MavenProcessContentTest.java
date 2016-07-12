package adobe.training.core;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ProcessContent.class)
public class MavenProcessContentTest {

  @Test
  public void testStripNonLettersOrNumbers() {
    ProcessContent pc = new ProcessContent();
    Assert.assertEquals("abc1", pc.stripNonLettersOrNumbers("a_b!c.1"));
    // el siguiente assert es para que falle el test.
    // Assert.assertEquals("abbc1", pc.stripNonLettersOrNumbers("a_b!c.1"));
  }

  @Test
  public void testGetContentPath() throws RepositoryException {
    final Session SESSION_MOCK = EasyMock.createMock(Session.class);
    final Node ROOT_NODE_MOCK = EasyMock.createMock(Node.class);
    EasyMock.expect(SESSION_MOCK.getRootNode()).andReturn(ROOT_NODE_MOCK);
    EasyMock.expect(ROOT_NODE_MOCK.hasNode(ProcessContent.CONTENT_NODENAME)).andReturn(true);
    final Node CONTENT_NODE_MOCK = EasyMock.createMock(Node.class);
    EasyMock.expect(ROOT_NODE_MOCK.getNode(ProcessContent.CONTENT_NODENAME)).andReturn(CONTENT_NODE_MOCK);
    EasyMock.expect(CONTENT_NODE_MOCK.getPath()).andReturn("/content");

    PowerMock.replay(SESSION_MOCK);
    PowerMock.replay(ROOT_NODE_MOCK);
    PowerMock.replay(CONTENT_NODE_MOCK);

    ProcessContent pc = new ProcessContent();
    Assert.assertEquals("/content", pc.getContentPath(SESSION_MOCK));

    // verify that all expected methods calls have been executed
    PowerMock.verifyAll();
  }
}
