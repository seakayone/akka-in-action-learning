import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object Greeter02Actor {
  def props(listener: Option[ActorRef]) = Props(new Greeter02Actor(listener))

  case class Greeting(message: String)

}

class Greeter02Actor(listener: Option[ActorRef]) extends Actor with ActorLogging {
  override def receive: Receive = {
    case Greeting(who) => {
      val message = "Hello " + who + "!"
      log.info(message)
      listener.foreach(l => l ! message)
    }
  }
}
