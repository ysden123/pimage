package com.stulsoft.pimage.popencv

import org.opencv.core._
import com.sun.javafx.iio.common.ImageTools
import org.opencv.core.{Core, CvType, Mat, Rect}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

/**
  * @author Yuriy Stul.
  */
object FourierDemo1 extends App {
  run("lena.png")
  run("people.jpg")

  def run(srcFileName:String): Unit = {
    println("==>run")
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    val image = Imgcodecs.imread(srcFileName)
    val dftImage = transformImage(image)
    // Save the visualized detection.
    val names = srcFileName.split("\\.")
    val rsltFileName = s"${names(0)}DFT.${names(1)}"
    println(s"Writing $rsltFileName")
    Imgcodecs.imwrite(rsltFileName, dftImage)
    println("<==run")
  }

  def transformImage(image: Mat): Mat = {
    // optimize the dimension of the loaded image
    val padded = optimizeImageDim(image)
    padded.convertTo(padded, CvType.CV_32F)
    Imgproc.cvtColor(padded, padded, Imgproc.COLOR_BGR2GRAY)
    // prepare the image planes to obtain the complex image
    val planes: java.util.List[Mat] = new java.util.ArrayList[Mat]
    planes.add(padded)
    planes.add(Mat.zeros(padded.size(), CvType.CV_32F))
    // prepare a complex image for performing the dft
    val complexImage = new Mat()
    Core.merge(planes, complexImage)

    //    ImageTools.outputMatProperties(complexImage)

    // dft
    Core.dft(complexImage, complexImage)

    // optimize the image resulting from the dft operation
    val magnitude = createOptimizedMagnitude(complexImage)

    magnitude
  }


  def antitransformImage(complexImage: Mat): Mat = {
    Core.idft(complexImage, complexImage)

    val planes = new java.util.ArrayList[Mat]
    val restoredImage = new Mat()
    Core.split(complexImage, planes)
    Core.normalize(planes.get(0), restoredImage, 0, 255, Core.NORM_MINMAX)
    restoredImage
  }

  def optimizeImageDim(image: Mat): Mat = {
    // init
    val padded = new Mat()
    // get the optimal rows size for dft
    val addPixelRows = Core.getOptimalDFTSize(image.rows)
    // get the optimal cols size for dft
    val addPixelCols = Core.getOptimalDFTSize(image.cols)
    // apply the optimal cols and rows size to the image
    Core.copyMakeBorder(image, padded, 0, addPixelRows - image.rows, 0, addPixelCols - image.cols, Core.BORDER_CONSTANT, Scalar.all(0))

    padded
  }

  def createOptimizedMagnitude(complexImage: Mat): Mat = {
    // init
    val planes: java.util.List[Mat] = new java.util.ArrayList[Mat]
    val mag = new Mat()
    // split the comples image in two planes
    Core.split(complexImage, planes)
    // compute the magnitude
    Core.magnitude(planes.get(0), planes.get(1), mag)

    // move to a logarithmic scale
    Core.add(Mat.ones(mag.size(), CvType.CV_32F), mag, mag)
    Core.log(mag, mag)
    // optionally reorder the 4 quadrants of the magnitude image
    val newMag = shiftDFT(mag)
    // normalize the magnitude image for the visualization since both JavaFX
    // and OpenCV need images with value between 0 and 255
    // convert back to CV_8UC1
    newMag.convertTo(newMag, CvType.CV_8UC1)
    Core.normalize(newMag, newMag, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1)

    // you can also write on disk the resulting image...
    // Imgcodecs.imwrite("../magnitude.png", mag);

    newMag
  }

  def shiftDFT(inImage: Mat): Mat = {
    val image = inImage.submat(new Rect(0, 0, inImage.cols & -2, inImage.rows & -2))
    val cx = inImage.cols / 2
    val cy = inImage.rows / 2

    val q0 = new Mat(image, new Rect(0, 0, cx, cy))
    val q1 = new Mat(image, new Rect(cx, 0, cx, cy))
    val q2 = new Mat(image, new Rect(0, cy, cx, cy))
    val q3 = new Mat(image, new Rect(cx, cy, cx, cy))

    val tmp = new Mat()
    q0.copyTo(tmp)
    q3.copyTo(q0)
    tmp.copyTo(q3)

    q1.copyTo(tmp)
    q2.copyTo(q1)
    tmp.copyTo(q2)

    tmp
  }
}
