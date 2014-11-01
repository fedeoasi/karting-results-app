package com.chicagof1.metrics

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.health.HealthCheckRegistry

object MetricsHolder {
  val metrics = new MetricRegistry()
  val healthChecks = new HealthCheckRegistry()
}
