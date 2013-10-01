package com.chicagof1.app

import org.scalatra._
import scalate.ScalateSupport
import com.chicagof1.data.DataManager

class KartingResultsServlet(dataManager: DataManager) extends KartingResultsAppStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

  get("/races") {
    contentType = "text/html"
    dataManager.races.keys.mkString("<br>")
  }
}
