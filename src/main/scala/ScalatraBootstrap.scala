import com.chicagof1.app._
import com.chicagof1.data.InMemoryDataManager
import grizzled.slf4j.Logging
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with Logging {
  override def init(context: ServletContext) {
    context.setInitParameter(org.scalatra.EnvironmentKey, "production")
    context.mount(new KartingResultsServlet(new InMemoryDataManager), "/*")
  }

}
