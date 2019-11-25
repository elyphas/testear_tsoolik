package manik1.manik1

import java.nio.ByteBuffer

import scala.util.Success
import akka.http.scaladsl.server.Directives
import manik1.manik1.twirl.Implicits._
import sloth.{Router, ServerFailure}
import monix.execution.Scheduler.Implicits.global
import scala.util.Failure

class WebService() extends Directives {

  val route = {
    pathSingleSlash {
      get {
        complete(manik1.manik1.html.index.render(""))
      }
    } ~ pathPrefix("assets" / Remaining) { file =>
        // optionally compresses the response with Gzip or Deflate  // if the client accepts compressed responses
        encodeResponse {
          getFromResource("public/" + file)
        }
      }
    }
}