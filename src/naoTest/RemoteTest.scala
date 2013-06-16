package naoTest

import akka.actor.ActorSystem
import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import akka.actor.ActorRef
import akka.actor.Props
import naogateway.value.NaoMessages._
import naogateway.value.NaoMessages.Conversions._
import naogateway.value.NaoVisionMessages._
import akka.actor.actorRef2Scala
import akka.event.Logging
import java.util.Arrays
import java.io._
import naogateway.value.HAWCamserverMessages.CamResponse


object RemoteTest extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("remoting", config.getConfig("remoting").withFallback(config))

  val naoActor = system.actorFor("akka://naogateway@192.168.1.101:2550/user/hanna")
  system.actorOf(Props[MyResponseTestActor])

  class MyResponseTestActor extends Actor {
    override def preStart = naoActor ! Connect

    def receive = {
      case (response: ActorRef, noResponse: ActorRef, vision: ActorRef) => {
        trace(response)
        trace(noResponse)
        trace(vision)
        // response ! Call('ALTextToSpeech, 'getVolume)
//               noResponse ! Call('ALTextToSpeech, 'say, List("hallo"))
        //        response ! Call('ALTextToSpeech, 'say, List("Stehen bleiben!"))
        vision ! VisionCall(Resolutions.k4VGA, ColorSpaces.kRGB, Frames._20)
//        vision ! VisionCall(Resolutions.kQQVGA, ColorSpaces.kRGB, Frames._20)
        //      vision ! RawVisionCall(Resolutions.k4VGA, ColorSpaces.kBGR, Frames._20)

      }
      case y: Array[Byte] => {
        println("ausgabe: ")
        println(Arrays.toString(y) + y.length)
      }
      case c: CamResponse => {
        sender ! Trigger
        trace(c.getImageData().size)
        trace(c.getImageData())
        
        val image = c.getImageData()
        
        val fos = new FileOutputStream("image2.jpg");
        try {
        	fos.write(image.toByteArray())
        }finally {
        	fos.close()
        }
        
        
//        val data = image.toByteArray() //Array("Five", "strings", "in", "a", "file!")
//        printToFile(new File("example.jpg"))(p => {
//          data.foreach(p.print)
//        })
      }
      case x => trace(x)

    }
    
//    def stream: Receive = {
//      case c:CamResponse =>
//    }

    def trace(a: Any) = log.info(a.toString)
    def error(a: Any) = log.warning(a.toString)
    def wrongMessage(a: Any, state: String) = log.warning("wrong message: " + a + " in " + state)
    import akka.event.Logging
    val log = Logging(context.system, this)
  }

  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }

  Thread.sleep(5000)
  system.shutdown
}