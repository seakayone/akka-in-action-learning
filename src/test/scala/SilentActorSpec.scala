import SilentActor.{GetState, SilentMessage}
import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import org.kleinb.StopSystemAfterAll
import org.scalatest.{MustMatchers, WordSpecLike}

class SilentActorSpec extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  "A Silent Actor" must {

    "change state when it receives a message, single threaded" in {
      val silentActor = TestActorRef[SilentActor]
      silentActor ! SilentMessage("whisper")
      silentActor.underlyingActor.state must contain("whisper")
    }

    "change state when it receives a message, multi-threaded" in {
      val silentActor = system.actorOf(Props[SilentActor], "s3")
      silentActor ! SilentMessage("whisper1")
      silentActor ! SilentMessage("whisper2")
      silentActor ! GetState(testActor)
      expectMsg(Vector("whisper1", "whisper2"))
    }
  }

}
