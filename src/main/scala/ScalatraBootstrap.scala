import com.chicagof1.app._
import com.chicagof1.data.DataProvider
import grizzled.slf4j.Logging
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with Logging {
  val oneMillion = 1000000

  override def init(context: ServletContext) {
    context.setInitParameter(org.scalatra.EnvironmentKey, "production")
    val start = System.nanoTime()
    val dataManager = DataProvider.dataManager()
    val stop = System.nanoTime()
    info(s"Loaded data manager in ${(stop - start) / oneMillion} millis")
    context.mount(new KartingResultsServlet(dataManager), "/*")
  }

}
