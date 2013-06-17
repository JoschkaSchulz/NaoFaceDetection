package naoFaceDetection

import java.awt.Toolkit
import javax.imageio.ImageIO
import java.io.File
import java.io.ByteArrayOutputStream

class TestWithoutNao extends App{
	val faceDetection = new FaceDetectJavaCV
	
	println("Erkannte Gesichter: " + this.faceDetection.detectFace(this.imageToByteArray("image.jpg")))
	
	def imageToByteArray(filePath:String):Array[Byte] = {
		val fnew= new File(filePath);
		val originalImage= ImageIO.read(fnew);
		val baos= new ByteArrayOutputStream();
		ImageIO.write(originalImage, "jpg", baos );
		return baos.toByteArray();
	}
}