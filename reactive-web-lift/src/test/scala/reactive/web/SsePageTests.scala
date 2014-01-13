package reactive
package web

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.concurrent.Eventually
import org.scalatest.time.SpanSugar
import scala.concurrent._
import ExecutionContext.Implicits._
import java.io._

class SsePageTests extends FunSuite with ShouldMatchers with Eventually with SpanSugar {
  test("SsePage") {
    lazy val page = new Page {
      val spc = new SsePageComponent(this) {
        override def maxTime = 2000
      }
      val pageComponents = spc :: Nil
    }
    val sw = new StringWriter
    val fut = future {
      page.spc.sseTransport.write(sw)
    }
    Thread.sleep(200)
    page.spc.sseTransport.queue("hello")
    Await.ready(fut, duration.Duration.Inf)

    eventually(timeout(3000.millis)) {
      sw.toString should equal ("id: 1\ndata: hello\n\n")
    }
  }
}