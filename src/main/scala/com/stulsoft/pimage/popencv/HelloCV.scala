package com.stulsoft.pimage.popencv

import org.opencv.core.{Core, CvType, Mat}

/**
  * @author Yuriy Stul.
  */
object HelloCV extends App {
  test1()

  def test1(): Unit = {
    println("==>test1")
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    val mat = Mat.eye(3, 3, CvType.CV_8UC1)
    println(mat.dump)

    println("<==test1")
  }
}
