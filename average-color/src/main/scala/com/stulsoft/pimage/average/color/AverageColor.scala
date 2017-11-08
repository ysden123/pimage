package com.stulsoft.pimage.average.color

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import com.typesafe.scalalogging.LazyLogging

/**
  * @author Yuriy Stul.
  */
object AverageColor extends App with LazyLogging {
  test()

  def test(): Unit = {
    logger.info("test started")
    calculateAverageColor("white.jpg")
    calculateAverageColor("blue.jpg")
    calculateAverageColor("white-blue.jpg")
    logger.info("test finished")
  }

  def calculateAverageColor(fileName: String): Unit = {
    var redBucket = 0
    var greenBucket = 0
    var blueBucket = 0
    var image: BufferedImage = null
    try {
      image = ImageIO.read(new File(fileName))
    }
    catch {
      case x: Throwable => logger.error("Failed read image with name {}. Error: {}", fileName, x.getMessage)
    }
    val colors = for {
      x <- 0 until image.getWidth
      y <- 0 until image.getHeight
    } yield new Color(image.getRGB(x, y))

    colors.foreach { c =>
      redBucket += c.getRed
      greenBucket += c.getGreen
      blueBucket += c.getBlue
    }
    redBucket = redBucket / colors.size
    greenBucket = greenBucket / colors.size
    blueBucket = blueBucket / colors.size

    logger.info("Average color in {} file: {},{},{}", fileName, redBucket, greenBucket, blueBucket)

  }
}
