package com.chicagof1.metrics

import javax.servlet.{ServletContextEvent, ServletContextListener}
import com.codahale.metrics.servlets.{HealthCheckServlet, MetricsServlet}

class MetricsServletContextListener extends ServletContextListener {
  import MetricsHolder._

  @Override
  def contextInitialized(sce: ServletContextEvent): Unit = {
    sce.getServletContext.setAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY, healthChecks)
    sce.getServletContext.setAttribute(MetricsServlet.METRICS_REGISTRY, metrics)
  }

  override def contextDestroyed(sce: ServletContextEvent): Unit = {
    sce.getServletContext.removeAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY)
    sce.getServletContext.removeAttribute(MetricsServlet.METRICS_REGISTRY)
  }
}