import com.chicagof1.app._
import com.chicagof1.data.{DataManager, InMemoryDataManager}
import com.chicagof1.jmx.DataManagerMetrics
import com.chicagof1.metrics.MetricsHolder
import com.codahale.metrics.{JmxReporter, ConsoleReporter}
import grizzled.slf4j.Logging
import java.util.concurrent.TimeUnit
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with Logging {
  override def init(context: ServletContext) {
    //context.setInitParameter(org.scalatra.EnvironmentKey, "production")
    val dataManager = new InMemoryDataManager
    initializeMetricsReporters(dataManager)
    context.mount(new KartingResultsServlet(dataManager), "/*")
  }

  override def destroy(context: ServletContext): Unit = {
    super.destroy(context)
  }

  private def initializeMetricsReporters(dataManager: DataManager): Unit = {
    val dmm = new DataManagerMetrics(dataManager)
    dmm.init()
    val reporter = ConsoleReporter.forRegistry(MetricsHolder.metrics)
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build()
    reporter.start(30, TimeUnit.MINUTES)
    val jmxReporter = JmxReporter.forRegistry(MetricsHolder.metrics).build()
    jmxReporter.start()
  }
}
