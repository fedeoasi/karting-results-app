import com.chicagof1.app._
import com.chicagof1.data.{DataManager, InMemoryDataManager}
import com.chicagof1.jmx.DataManagerMetrics
import com.chicagof1.metrics.MetricsHolder
import com.chicagof1.persistence.{PersistenceManager, ProdPersistenceManager}
import com.codahale.metrics.{JmxReporter, ConsoleReporter}
import grizzled.slf4j.Logging
import java.util.concurrent.TimeUnit
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with Logging {
  var jmxReporter: JmxReporter = _
  var reporter: ConsoleReporter = _
  var persistenceManager: PersistenceManager = _

  override def init(context: ServletContext) {
    if (AppLocation.isProduction) {
      context.setInitParameter(org.scalatra.EnvironmentKey, "production")
      persistenceManager = new ProdPersistenceManager("chicagof1")
    } else {
      persistenceManager = new ProdPersistenceManager("chicagof1-dev")
    }
    val dataManager = new InMemoryDataManager
    initializeMetricsReporters(dataManager)
    context.mount(new KartingResultsServlet(dataManager), "/*")
  }

  override def destroy(context: ServletContext): Unit = {
    info("Shutting down")
    if(jmxReporter != null) {
      info("Stopping JMX reporter")
      jmxReporter.stop()
    }
    if(reporter != null) {
      info("Stopping console reporter")
      reporter.stop()
    }
    super.destroy(context)
  }

  private def initializeMetricsReporters(dataManager: DataManager): Unit = {
    val dmm = new DataManagerMetrics(dataManager)
    dmm.init()
    reporter = ConsoleReporter.forRegistry(MetricsHolder.metrics)
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build()
    reporter.start(30, TimeUnit.MINUTES)
    jmxReporter = JmxReporter.forRegistry(MetricsHolder.metrics).build()
    jmxReporter.start()
  }
}
