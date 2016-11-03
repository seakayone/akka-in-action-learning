import akka.actor.{Actor, ActorLogging}

case class Greeting(message: String)

class GreeterActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case Greeting(message) => log.info("Hello {}!", message)
  }
}
