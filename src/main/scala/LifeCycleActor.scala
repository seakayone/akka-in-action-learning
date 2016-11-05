import akka.actor.{Actor, ActorLogging}

class LifeCycleActor extends Actor with ActorLogging {

  System.out.println("Constructor")

  override def preStart(): Unit = println("prestart")

  override def postStop(): Unit = println("postStop")

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("preRestart")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    println("postRestart")
    super.postRestart(reason)
  }

  override def receive: Receive = {
    case "restart" => throw new IllegalStateException("force restart")
    case msg: AnyRef =>
      println("Receive")
      sender() ! msg
  }

}
