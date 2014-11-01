package com.chicagof1.jmx

import com.chicagof1.data.DataManager
import com.chicagof1.metrics.MetricsHolder
import com.codahale.metrics.{Gauge, MetricRegistry}

class DataManagerMetrics(dataManager: DataManager) {
  import MetricsHolder.metrics

  def init(): Unit = {
    registerSize(dataManager.racers, "racers")
    registerSize(dataManager.videos, "videos")
    registerSize(dataManager.editionsWithRaces, "editions")
  }

  private def registerSize[T](list: List[T], name: String): Unit = {
    metrics.register(MetricRegistry.name(dataManager.getClass, name, "size"),
      new Gauge[Int] { override def getValue: Int = list.size }
    )
  }
}
