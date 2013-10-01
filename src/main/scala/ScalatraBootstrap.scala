import com.chicagof1.app._
import com.chicagof1.data.DataProvider
import grizzled.slf4j.Logging
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with Logging {
  override def init(context: ServletContext) {
    val dataManager = DataProvider.dataManager()
    context.mount(new KartingResultsServlet(dataManager), "/*")
  }

}
