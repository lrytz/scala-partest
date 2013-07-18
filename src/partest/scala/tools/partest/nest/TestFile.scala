/* NEST (New Scala Test)
 * Copyright 2007-2008 LAMP/EPFL
 * @author Philipp Haller
 */

// $Id$

package scala.tools.partest.nest

import java.io.{File, BufferedReader, FileReader}
import scala.tools.nsc.Settings

class TestFile(kind: String, val file: File, val fileManager: FileManager) {
  val dir = file.getParentFile
  val dirpath = dir.getAbsolutePath
  val fileBase: String = basename(file.getName)

  // @mutates settings
  protected def baseSettings(settings: Settings) {
    settings.classpath.value = dirpath
    settings.outdir.value = {
      val outDir = new File(dir, fileBase + "-" + kind + ".obj")
      if (!outDir.exists)
        outDir.mkdir()
      outDir.toString
    }

    // add additional flags found in 'testname.flags'
    val flagsFile = new File(dir, fileBase + ".flags")
    if (flagsFile.exists) {
      val reader = new BufferedReader(new java.io.FileReader(flagsFile))
      val flags = reader.readLine
      if (flags ne null)
        settings.parseParams(flags, error)
    }
  }

  def defineSettings(settings: Settings) {
    baseSettings(settings)
  }

  private def basename(name: String): String = {
    val inx = name.lastIndexOf(".")
    if (inx < 0) name else name.substring(0, inx)
  }

  override def toString(): String = kind+" "+file
}

case class PosTestFile(override val file: File, override val fileManager: FileManager) extends TestFile("pos", file, fileManager) {
  override def defineSettings(settings: Settings) {
    baseSettings(settings)
    settings.classpath.value = fileManager.CLASSPATH
    //println("settings.classpath.value="+settings.classpath.value)
  }
}

case class NegTestFile(override val file: File, override val fileManager: FileManager) extends TestFile("neg", file, fileManager) {
  override def defineSettings(settings: Settings) {
    baseSettings(settings)
    settings.classpath.value = fileManager.CLASSPATH
    //println("settings.classpath.value="+settings.classpath.value)
  }
}

case class RunTestFile(override val file: File, override val fileManager: FileManager) extends TestFile("run", file, fileManager) {
  override def defineSettings(settings: Settings) {
    baseSettings(settings)
    settings.classpath.value = fileManager.CLASSPATH
  }
}

case class JvmTestFile(override val file: File, override val fileManager: FileManager) extends TestFile("jvm", file, fileManager) {
  override def defineSettings(settings: Settings) {
    baseSettings(settings)
    settings.classpath.value = fileManager.CLASSPATH
    //println("settings.classpath.value="+settings.classpath.value)
  }
}

case class Jvm5TestFile(override val file: File, override val fileManager: FileManager) extends TestFile("jvm5", file, fileManager) {
  override def defineSettings(settings: Settings) {
    baseSettings(settings)
    settings.classpath.value = fileManager.CLASSPATH
    settings.target.value = "jvm-1.5"
    //println("settings.classpath.value="+settings.classpath.value)
  }
}

case class ShootoutTestFile(override val file: File, override val fileManager: FileManager) extends TestFile("shootout", file, fileManager) {
  override def defineSettings(settings: Settings) {
    baseSettings(settings)
    settings.classpath.value = fileManager.CLASSPATH
    //println("settings.classpath.value="+settings.classpath.value)
    settings.outdir.value = file.getParent
  }
}
