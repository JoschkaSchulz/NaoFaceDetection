import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_stitching.WarperCreator;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 *
 * @author Samuel Audet
 */
public class FaceApplet extends Applet implements Runnable {

    private CvHaarClassifierCascade classifier = null;
    private CvHaarClassifierCascade classifier2 = null;
    private CvMemStorage storage = null;
    private FrameGrabber grabber = null;
    private IplImage grabbedImage = null, grayImage = null, smallImage = null;
    private CvSeq faces = null;
    private CvSeq faces2 = null;
    private CvSeq faces3 = null;
    private boolean stop = false;
    private Exception exception = null;
    
    private static final int SPEEDUP = 2;

    private String classiferName;
    private String classiferName2;
    
    @Override public void init() {
        try {
            // Load the classifier file from Java resources.
            classiferName = "haarcascade_frontalface_alt2.xml";
            classiferName2 = "haarcascade_profileface.xml";
            File classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");
            File classifierFile2 = Loader.extractResource(classiferName2, null, "classifier", ".xml");
            if (classifierFile == null || classifierFile2 == null || classifierFile.length() <= 0 || classifierFile2.length() <= 0) {
                throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
            	//System.err.println("Fehler Datei haarcascade_blablabla nicht gefunden!");
            }

            // Preload the opencv_objdetect module to work around a known bug.
            Loader.load(opencv_objdetect.class);
            classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
            classifier2 = new CvHaarClassifierCascade(cvLoad(classifierFile2.getAbsolutePath()));
            classifierFile.delete();
            if (classifier.isNull()) {
                throw new IOException("Could not load the classifier file.");
            }

            storage = CvMemStorage.create();
        } catch (Exception e) {
            if (exception == null) {
                exception = e;
                repaint();
            }
        }
        
        //Fenstergröße anpassen
        this.setSize(960, 640);
    }

    @Override public void start() {
        try {
            new Thread(this).start();
        } catch (Exception e) {
            if (exception == null) {
                exception = e;
                repaint();
            }
        }
    }

    public void run() {
        try {
            try {
                grabber = FrameGrabber.createDefault(0);
                grabber.setImageWidth(getWidth());
                grabber.setImageHeight(getHeight());
                grabber.start();
                grabbedImage = grabber.grab();
            } catch (Exception e) {
                if (grabber != null) grabber.release();
                grabber = new OpenCVFrameGrabber(0);
                grabber.setImageWidth(getWidth());
                grabber.setImageHeight(getHeight());
                grabber.start();
                grabbedImage = grabber.grab();
            }
            grayImage  = IplImage.create(grabbedImage.width(),   grabbedImage.height(),   IPL_DEPTH_8U, 1);
            smallImage = IplImage.create(grabbedImage.width()/SPEEDUP, grabbedImage.height()/SPEEDUP, IPL_DEPTH_8U, 1);
            
            stop = false;
            while (!stop && (grabbedImage = grabber.grab()) != null) {
                if (faces == null) {
                    cvClearMemStorage(storage);
                    cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
                    cvResize(grayImage, smallImage, CV_INTER_AREA);
                    
                    faces = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
                    cvGetImage(rotateImage(smallImage.asCvMat(), 10),smallImage);
                    faces2 = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
                    cvGetImage(rotateImage(smallImage.asCvMat(), -20),smallImage);
                    faces3 = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
                    
                    faces2 = cvHaarDetectObjects(smallImage, classifier2, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
                    repaint();
                }
            }
            grabbedImage = grayImage = smallImage = null;
            grabber.stop();
            grabber.release();
            grabber = null;
        } catch (Exception e) {
            if (exception == null) {
                exception = e;
                repaint();
            }
        }
    }

    @Override public void update(Graphics g) {
        paint(g);
    }

    private int runs = 0;
    @Override public void paint(Graphics g) {
        if (grabbedImage != null) {
//        	cvGetImage(rotateImage(grabbedImage.asCvMat(), 10),grabbedImage);
            BufferedImage image = grabbedImage.getBufferedImage(2.2/grabber.getGamma());
            Graphics2D g2 = image.createGraphics();
            int anzahl = 0;
            if (faces != null) {
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(2));
                int total = anzahl = faces.total();
                for (int i = 0; i < total; i++) {
                    CvRect r = new CvRect(cvGetSeqElem(faces, i));
                    g2.drawRect(r.x()*SPEEDUP, r.y()*SPEEDUP, r.width()*SPEEDUP, r.height()*SPEEDUP);
                }
                faces = null;
            }
            int anzahl2 = 0;
            if(faces2 != null) {
            	g2.setColor(Color.GREEN);
            	g2.setStroke(new BasicStroke(2));
            	int total = anzahl2 = faces2.total();
            	for(int i = 0; i < total; i++) {
            		CvRect r = new CvRect(cvGetSeqElem(faces2, i));
                    g2.drawRect(r.x()*SPEEDUP, r.y()*SPEEDUP, r.width()*SPEEDUP, r.height()*SPEEDUP);
            	}
            	faces2 = null;
            }
            g.drawImage(image, 0, 0, 640, 480, null);
            g.clearRect(640, 0, 320, 75);
//            g.clearRect(0, 0, 160, 50);
            g.drawString(classiferName+": "+anzahl, 645, 25);
            g.drawString(classiferName2+": "+anzahl2, 645, 35);
            g.drawString("Durchlauf:"+runs++, 645, 55);
        }
        if (exception != null) {
            int y = 0, h = g.getFontMetrics().getHeight();
            g.drawString(exception.toString(), 5, y += h);
            for (StackTraceElement e : exception.getStackTrace()) {
                g.drawString("        at " + e.toString(), 5, y += h);
            }
        }
    }

    public CvMat rotateImage(CvMat input, int angle) {
        CvPoint2D32f center = new CvPoint2D32f(input.cols() / 2.0F,
                input.rows() / 2.0F);

        CvMat rotMat = cvCreateMat(2, 3, CV_32F);
        cv2DRotationMatrix(center, angle, 1, rotMat);
        CvMat dst = cvCreateMat(input.rows(), input.cols(), input.type());
        cvWarpAffine(input, dst, rotMat);
        return dst;

    }
    
    @Override public void stop() {
        stop = true;
    }

    @Override public void destroy() { }
}
