package com.chicagof1.app

object AppLocation {
  lazy val currentLocation: String = {
    if (isProduction) "www.chicagof1.net" else "localhost:8080"
  }

  lazy val isProduction: Boolean = {
    val production = System.getenv("CHI_F1_PRODUCTION")
    production != null
  }
}
