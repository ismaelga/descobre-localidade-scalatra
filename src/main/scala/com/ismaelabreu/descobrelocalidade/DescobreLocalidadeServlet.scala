package com.ismaelabreu.descobrelocalidade

import org.scalatra._
import scalate.ScalateSupport

class DescobreLocalidadeServlet extends DescobreLocalidadeScalatraStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }
  
}
