package com.stulsoft.pimage.average

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

  def calculateAverageColor(fileName: String): Color = {
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

    val totals = colors.foldLeft((0,0,0)){(z,c) => (z._1 + c.getRed, z._2 + c.getGreen, z._3 + c.getBlue)}
    val averageColor = new Color(totals._1 / colors.size, totals._2 / colors.size, totals._3 / colors.size )

    logger.info("Average color in {} file: {},{},{}", fileName, averageColor.getRed, averageColor.getGreen, averageColor.getBlue)

    averageColor
  }
}
