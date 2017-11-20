package com.stulsoft.pimage.popencv

import org.opencv.core.{Core, MatOfRect, Point, Scalar}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier

/** Detects faces in an image, draws boxes around them, and writes the results
  * to "peopleFaceDetection.jpg".
  *
  * @see [[https://docs.opencv.org/master/d9/d52/tutorial_java_dev_intro.html]]
  * @author Yuriy Stul.
  */
object DetectFaceDemo2 extends App {
  run()

  def run(): Unit = {
    println("==>run")
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    // Create a face detector from the cascade file in the resources
    // directory.
    val faceDetector = new CascadeClassifier()
    val loadResult = faceDetector.load("data/lbpcascade_frontalface_improved.xml")
    println(s"Loaded image: $loadResult")
    val image = Imgcodecs.imread("people.jpg")

    // Detect faces in the image.
    // MatOfRect is a special container class for Rect.
    val faceDetections = new MatOfRect()
    faceDetector.detectMultiScale(image, faceDetections)

    println(s"Detected ${faceDetections.toArray.length} faces")

    // Draw a bounding box around each face.
    faceDetections.toArray.foreach { rect =>
      Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0))
    }

    // Save the visualized detection.
    val fileName = "peopleFaceDetection.jpg"
    println(s"Writing $fileName")
    Imgcodecs.imwrite(fileName, image)
    println("<==run")
  }
}
