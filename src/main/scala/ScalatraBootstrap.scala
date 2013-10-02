import com.ismaelabreu.descobrelocalidade._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    Locations
    context.mount(new DescobreLocalidadeServlet, "/*")
  }
}
