import com.chicagof1.app._
import com.chicagof1.data.{DataManager, InMemoryDataManager}
import com.chicagof1.jmx.Data
import grizzled.slf4j.Logging
import java.lang.management.ManagementFactory
import javax.management.ObjectName
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with Logging {
  val dataManagerBeanName = new ObjectName("com.chicagof1:type=DataManager")

  override def init(context: ServletContext) {
    context.setInitParameter(org.scalatra.EnvironmentKey, "production")
    val dataManager = new InMemoryDataManager
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
}
