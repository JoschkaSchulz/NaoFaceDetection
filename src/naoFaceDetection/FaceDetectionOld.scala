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

class FaceDetectJavaCV {
	val classiferName = "haarcascade_frontalface_alt2.xml"
	val classiferName2 = "haarcascade_profileface.xml"
	  
			//Load the classifier from Resource
	val classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml")
	val classifierFile2 = Loader.extractResource(classiferName2, null, "classifier", ".xml")
	
	if (classifierFile == null || classifierFile2 == null || classifierFile.length() <= 0 || classifierFile2.length() <= 0) {
	    throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.")
	}
	
	//Loader.load(opencv_objdetect.class)
	val classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()))
	val classifier2 = new CvHaarClassifierCascade(cvLoad(classifierFile2.getAbsolutePath()))
	classifierFile.delete()
	if (classifier.isNull()) {
	    throw new IOException("Could not load the classifier file.")
	}
	
	def detectFace(imageData: Array[Byte]) = {
	  
		def inputStream(img: Array[Byte]): Try[ByteArrayInputStream] = {
		    Try(new ByteArrayInputStream(img))
		  }
	  
        inputStream(imageData) match {
          case Success(inputStream) => val in = inputStream
          case Failure(f) => println("Fehler beim holen des Bildes")
        }
	  
		val img = IplImage.createFrom(inputStream(imageData))
		val grayImage  = IplImage.create(grabbedImage.width(),   grabbedImage.height(),   IPL_DEPTH_8U, 1)
		val smallImage = IplImage.create(grabbedImage.width()/SPEEDUP, grabbedImage.height()/SPEEDUP, IPL_DEPTH_8U, 1)
	}
	
}