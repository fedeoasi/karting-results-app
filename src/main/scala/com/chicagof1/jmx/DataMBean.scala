package com.chicagof1.jmx

import com.chicagof1.data.DataManager

trait DataMBean {
  def getRacersSize: Int
  def getVideosSize: Int
  def reload: Unit
}

class Data(dataManager: DataManager) extends DataMBean {
  override def getRacersSize: Int = dataManager.racers.size
  override def getVideosSize: Int = dataManager.videos.size
  override def reload: Unit = dataManager.reload()
}
