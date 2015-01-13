import com.chicagof1.app._
import com.chicagof1.auth.MyCallbackFilter
import com.chicagof1.data.{DataManager, InMemoryDataManager}
import com.chicagof1.jmx.DataManagerMetrics
import com.chicagof1.metrics.MetricsHolder
import com.chicagof1.persistence.{PersistenceManager, ProdPersistenceManager}
import com.codahale.metrics.{JmxReporter, ConsoleReporter}
import grizzled.slf4j.Logging
import java.util
import java.util.concurrent.TimeUnit
import org.scalatra._
import javax.servlet.{DispatcherType, ServletContext}

class ScalatraBootstrap extends LifeCycle with Logging {
  var jmxReporter: JmxReporter = _
  var reporter: ConsoleReporter = _
  var persistenceManager: PersistenceManager = _

  override def init(context: ServletContext) {
    if (AppLocation.isProduction) {
      context.setInitParameter(org.scalatra.EnvironmentKey, "production")
    }
    setupPersistenceManager()
    val dataManager = new InMemoryDataManager
    context.mount(new KartingResultsServlet(dataManager), "/*")
    setupCallbackFilter(context)
    initializeMetricsReporters(dataManager)
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

  private def setupPersistenceManager(): Unit = {
    if (AppLocation.isProduction) {
      persistenceManager = new ProdPersistenceManager("chicagof1")
    } else {
      persistenceManager = new ProdPersistenceManager("chicagof1-dev")
    }
  }

  private def setupCallbackFilter(context: ServletContext): Unit = {
    val callbackFilter = new MyCallbackFilter(persistenceManager)
    val fr = context.addFilter("CallbackFilter", callbackFilter)
    fr.setInitParameter("clientsFactory", "com.chicagof1.auth.MyClientsFactory")
    fr.setInitParameter("defaultUrl", "/")
    fr.addMappingForUrlPatterns(util.EnumSet.of(DispatcherType.REQUEST), false, "/callback")
  }
}
