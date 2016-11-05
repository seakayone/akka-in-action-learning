import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import org.kleinb.StopSystemAfterAll
import org.scalatest.WordSpecLike

class LifecycleActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with StopSystemAfterAll {

  "A LifecycleActorTest" must {

    "have all lifecycle events" in {
      val testActorRef = system.actorOf(Props[LifeCycleActor], "LifeCycleActor")
      testActorRef ! "restart"
      testActorRef.tell("msg", testActor)
      expectMsg("msg")
      system.stop(testActorRef)
      Thread.sleep(1000)
    }
  }

}
