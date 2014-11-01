package com.chicagof1

import com.javamex.classmexer.MemoryUtil
import com.chicagof1.data.InMemoryDataManager
import com.javamex.classmexer.MemoryUtil.VisibilityFilter

object TestMemory {
  def main(args: Array[String]) = {
    val dataManager = new InMemoryDataManager
    println(s"Size of DataManager: ${MemoryUtil.deepMemoryUsageOf(dataManager, VisibilityFilter.ALL)} bytes")
    println(s"Size of Current Championship: ${MemoryUtil.deepMemoryUsageOf(dataManager.currentChampionship)} bytes")
    println(s"Size of DataManager: ${MemoryUtil.deepMemoryUsageOf(dataManager)} bytes")
  }
}
