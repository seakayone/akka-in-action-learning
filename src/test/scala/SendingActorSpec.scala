
import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.kleinb.StopSystemAfterAll
import org.scalatest.{MustMatchers, WordSpecLike}

import scala.util.Random

class SendingActorSpec extends TestKit(ActorSystem("testssystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  import SendingActor._

  "A Sending Actor" must {
    "send a message to another actor when it has finished processing" in {
      val props = SendingActor.props(testActor)
      val sendingActor = system.actorOf(props, "sendingActor")

      val size = 1000
      val maxInclusive = 10000

      def randomEvents() = (0 until size).map { _ =>
        Event(Random.nextInt(maxInclusive))
      }.toVector

      val unsorted = randomEvents()
      val sortEvents = SortEvents(unsorted)

      sendingActor ! sortEvents

      expectMsgPF() {
        case SortedEvents(events) =>
          events.size must be(size)
          unsorted.sortBy(_.id) must be(events)
      }
    }
  }
}
