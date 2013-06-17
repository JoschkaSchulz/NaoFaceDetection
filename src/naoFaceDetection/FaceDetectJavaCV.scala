package naoFaceDetection

import java.applet.Applet
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import com.googlecode.javacpp.Loader
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade
import com.googlecode.javacv.cpp.opencv_core._
import com.googlecode.javacv.cpp.opencv_imgproc._
import com.googlecode.javacv.cpp.opencv_objdetect._
import com.googlecode.javacv.cpp.opencv_highgui._
import com.googlecode.javacv.FrameGrabber
import java.io.InputStream
import scala.util.Success
import java.io.ByteArrayInputStream
import scala.util.Failure
import scala.util.Try
import scala.io.Source
import javax.imageio.ImageIO

class FaceDetectJavaCV {
	
	//==========================================
	//Einstellungen
	//==========================================
	
  
	/*
	 * Variable: Divided
	 * Typ: Integer
	 * Beschreibung:
	 * Die Variable gibt an wie sehr das Bild verkleinert werden soll.
	 * Durch erhöhen dieses Wertes lässt sich der erkennungsprozess 
	 * um einiges Beschleunigen. 
	 * 
	 * ACHTUNG: 
	 * Die beste erkennung scheint nicht auf auf dem größten Bild zu
	 * funktionieren, Gesichter können doppelt erkannt werden oder
	 * es können Gesichter erkannt werden welche nicht existieren. Ein
	 * geeigniter WErt ist 4.
	 */
	val Divided = 4
	
	//==========================================
	// Variablen & Konstrucktor
	//==========================================
	
	//Methode die verwendet werden soll
	val classiferName = "haarcascade_frontalface_alt2.xml"
	  
	//Load the classifier from Resource
	val classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml")
	
	if (classifierFile == null || classifierFile.length() <= 0) {
	    throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.")
	}
	
	//Loader.load(opencv_objdetect.class)
	val classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()))
	classifierFile.delete()
	if (classifier.isNull()) {
	    throw new IOException("Could not load the classifier file.")
	}
	
	//==========================================
	// Methoden
	//==========================================
	
	def detectFace(imageData: Array[Byte]) : Int = {
	  
		def inputStream(img: Array[Byte]): Try[ByteArrayInputStream] = {
		    Try(new ByteArrayInputStream(img))
		  }
	  
        inputStream(imageData) match {
          case Success(inputStream) => val in = inputStream
          case Failure(f) => println("Fehler beim holen des Bildes")
        }
	  
        //Lege einen storage an
        val storage = CvMemStorage.create()
        
		//Hole Bild von Nao(.jpg) und wandle es in ein IplImage um!
        val in = new ByteArrayInputStream(imageData)
    	val bImageFromConvert = ImageIO.read(in)
        val img = IplImage.createFrom(bImageFromConvert)
		val grayImage  = IplImage.create(img.width(),   img.height(),   IPL_DEPTH_8U, 1)
		val smallImage = IplImage.create(img.width()/Divided, img.height()/Divided, IPL_DEPTH_8U, 1)
	
		cvClearMemStorage(storage)
		cvCvtColor(img, grayImage, CV_BGR2GRAY)
		cvResize(grayImage,smallImage,CV_INTER_AREA)
		
		val faces = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING)
		
		if(faces != null) {
			return faces.total()
		}else{
			return 0
		}
		
	}
	
}