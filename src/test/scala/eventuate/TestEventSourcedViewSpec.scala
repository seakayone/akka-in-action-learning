import akka.actor._
import akka.testkit._
import com.rbmhtechnology.eventuate.EventsourcingProtocol._
import com.rbmhtechnology.eventuate._
import org.kleinb.StopSystemAfterAll
import org.scalatest._

object TestEventSourcedViewSpec {

  val emitterIdA = "A"
  val logIdA = "logA"

  case class GetState(receiver: ActorRef)

  class TestEventsourcedView(
                              val logProbe: ActorRef,
                              val msgProbe: ActorRef) extends EventsourcedView {

    val id = emitterIdA
    val eventLog = logProbe

    var internalState = Vector[String]()

    override def onCommand = {
      case GetState(receiver) => receiver ! internalState
    }

    override def onEvent = {
      case evt: String => {
        internalState = internalState :+ evt
        msgProbe ! evt
      }
    }
  }

  val event1a = event("a", 1L)
  val event1b = event("b", 2L)
  val event1c = event("c", 3L)

  def event(payload: Any, sequenceNr: Long): DurableEvent =
    DurableEvent(payload, emitterIdA, None, Set(), 0L, timestamp(sequenceNr), logIdA, logIdA, sequenceNr)

  def timestamp(a: Long = 0L, b: Long = 0L) = (a, b) match {
    case (0L, 0L) => VectorTime()
    case (a, 0L) => VectorTime(logIdA -> a)
  }

}


class TestEventSourcedViewSpec extends TestKit(ActorSystem("test"))
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterEach
  with StopSystemAfterAll {

  import TestEventSourcedViewSpec._

  var logProbe: TestProbe = _
  var msgProbe: TestProbe = _

  override def beforeEach(): Unit = {
    logProbe = TestProbe()
    msgProbe = TestProbe()
  }

  def unrecoveredEventsourcedView(): ActorRef =
    system.actorOf(Props(new TestEventsourcedView(logProbe.ref, msgProbe.ref)))

  def recoveredEventsourcedView(): ActorRef = {
    val actor = unrecoveredEventsourcedView()
    var catchId: Int = -1
    logProbe.expectMsgPF() {
      case LoadSnapshot(emitter, instId) => {
        catchId = instId
        emitter must be(emitterIdA)
      }
    }
    logProbe.sender() ! LoadSnapshotSuccess(None, catchId)
    logProbe.expectMsg(Replay(1L, Some(actor), catchId))
    logProbe.sender() ! ReplaySuccess(Nil, 1L, catchId)
    actor
  }

  "An EventsourcedView" must {
    "process a single event" in {
      val actor = recoveredEventsourcedView()

      actor ! Written(event1a)

      msgProbe.expectMsg("a")
      msgProbe.expectNoMsg()
    }

    "process two events" in {
      val actor = recoveredEventsourcedView()

      actor ! Written(event1a)
      actor ! Written(event1b)

      msgProbe.expectMsg("a")
      msgProbe.expectMsg("b")
      msgProbe.expectNoMsg()
    }

    "ignore a third event with invalid sequence nr." in {
      val actor = recoveredEventsourcedView()

      actor ! Written(event1a)
      actor ! Written(event1b)
      actor ! Written(event1a)

      msgProbe.expectMsg("a")
      msgProbe.expectMsg("b")
      msgProbe.expectNoMsg()
    }

    "have an internal state" in {
      val actor = recoveredEventsourcedView()

      actor ! Written(event1a)

      msgProbe.expectMsg("a")

      actor ! GetState(msgProbe.ref)
      msgProbe.expectMsg(Vector("a"))

      msgProbe.expectNoMsg()
    }
  }

}
