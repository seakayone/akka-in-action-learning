import akka.actor.{ActorSystem, UnhandledMessage}
import akka.testkit.TestKit
import org.kleinb.StopSystemAfterAll
import org.scalatest.WordSpecLike

class Greeter02ActorSpec extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with StopSystemAfterAll {

  "The Greeter" must {
    "say Hello World! when a Greeting(\"World\") is sent to it" in {
      val props = Greeter02Actor.props(Some(testActor))
      val greeter = system.actorOf(props, "greeter01-1")
      greeter ! Greeting("World")
      expectMsg("Hello World!")
    }

    "say something else and see what happens" in {
      val props = Greeter02Actor.props(Some(testActor))
      val greeter = system.actorOf(props, "greeter01-2")

      system.eventStream.subscribe(testActor, classOf[UnhandledMessage])
      greeter ! "World"
      expectMsg(UnhandledMessage("World", system.deadLetters, greeter))
    }
  }

}
