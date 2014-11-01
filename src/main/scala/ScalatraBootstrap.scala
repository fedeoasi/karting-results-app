import com.chicagof1.app._
import com.chicagof1.data.{DataManager, InMemoryDataManager}
import com.chicagof1.jmx.Data
import com.chicagof1.metrics.MetricsHolder
import com.codahale.metrics.{JmxReporter, ConsoleReporter}
import grizzled.slf4j.Logging
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import javax.management.ObjectName
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with Logging {
  val dataManagerBeanName = new ObjectName("com.chicagof1:type=DataManager")

  override def init(context: ServletContext) {
    context.setInitParameter(org.scalatra.EnvironmentKey, "production")
    val dataManager = new InMemoryDataManager
    initializeMetricsReporters()
    registerBeans(dataManager)
    context.mount(new KartingResultsServlet(dataManager), "/*")
  }

  private def registerBeans(dataManager: DataManager): Unit = {
    val dataMBean = new Data(dataManager)
    val mbs = ManagementFactory.getPlatformMBeanServer
    mbs.registerMBean(dataMBean, dataManagerBeanName)
  }

  override def destroy(context: ServletContext): Unit = {
    val mbs = ManagementFactory.getPlatformMBeanServer
    mbs.unregisterMBean(dataManagerBeanName)
    super.destroy(context)
  }

  private def initializeMetricsReporters(): Unit = {
    val reporter = ConsoleReporter.forRegistry(MetricsHolder.metrics)
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build()
    reporter.start(30, TimeUnit.SECONDS)
    val jmxReporter = JmxReporter.forRegistry(MetricsHolder.metrics).build()
    jmxReporter.start()
  }
}
