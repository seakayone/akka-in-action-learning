import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.kleinb.StopSystemAfterAll
import org.scalatest.WordSpecLike

class EchoActorSpec extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with ImplicitSender
  with StopSystemAfterAll {

  "Echo Actor" must {
    "echo any message sent" in {
      val props = Props(new EchoActor)
      val echo = system.actorOf(props)

      echo ! "echo this message"
      expectMsg("echo this message")
    }
  }

}
