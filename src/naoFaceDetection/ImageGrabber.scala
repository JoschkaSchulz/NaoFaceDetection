package naoFaceDetection

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor
import akka.actor.ActorRef
import naogateway.value.NaoVisionMessages._
import naogateway.value.HAWCamserverMessages.CamResponse
import naogateway.value.NaoMessages._
import akka.actor.actorRef2Scala

object ImageGrabber extends App{
	val config = ConfigFactory.load()
	val system = ActorSystem("remoting", config.getConfig("remoting").withFallback(config))
	
	 val naoActor = system.actorFor("akka://naogateway@192.168.1.101:2552/user/nila")
	 system.actorOf(Props[ResponseActor])
	 
	 class ResponseActor extends Actor {
		override def preStart = naoActor ! Connect
		
		def receive = {
		  case (response: ActorRef, noResponse: ActorRef, vision: ActorRef) => {
		    vision ! VisionCall(Resolutions.k4VGA, ColorSpaces.kRGB, Frames._20)
		  }
		  case c: CamResponse => {
		    println(c.getImageData().size())
		  }
		}
	}
}