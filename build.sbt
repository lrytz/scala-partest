import VersionKeys._

scalaModuleSettings

name                       := "scala-partest"

version                    := "1.0.9-SNAPSHOT"

scalaVersion               := crossScalaVersions.value.head
crossScalaVersions         := {
  val allCrossVersions = Seq("2.11.6", "2.12.0-M1", "2.12.0-M2")

  val MajDotMinDot = """(\d+)\.(\d+)\..*""".r

  def needsJava8(scalaVersion: String) = {
    val MajDotMinDot(major, minor) = scalaVersion
    major.toInt == 2 && minor.toInt >= 12 || major.toInt > 2
  }

  object javaVersion {
    override val toString = System.getProperty("java.version")
    private val (javaMajor, javaMinor) = {
      val MajDotMinDot(major, minor) = toString
      (major.toInt, minor.toInt)
    }
    def isExactly(minor: Int): Boolean = javaMajor == 1 && javaMinor == minor
    def isAtLeast(minor: Int): Boolean = javaMajor == 1 && javaMinor >= minor || javaMajor > 1 // haha
  }

  def publishVersions: Seq[String] = {
    if (javaVersion.isExactly(6))
      allCrossVersions.filterNot(needsJava8)
    else if (javaVersion.isExactly(8))
      allCrossVersions.filter(needsJava8)
    else
      sys.error(s"don't know what Scala versions to build on $javaVersion")
  }

  def testVersions: Seq[String] = {
    if (javaVersion.isAtLeast(8))
      allCrossVersions
    else
      allCrossVersions.filterNot(needsJava8)
  }

  val allowAnyJVM = util.Properties.propIsSet("sbt.allowCrossBuildingAnyJVM")
  if (allowAnyJVM) {
    testVersions
  } else {
    publishVersions
  }
}

scalaXmlVersion            := "1.0.4"

scalaCheckVersion          := "1.11.6"

// TODO: enable "-Xfatal-warnings" for nightlies,
// off by default because we don't want to break scala/scala pr validation due to deprecation
// don't use for doc scope, scaladoc warnings are not to be reckoned with
scalacOptions in (Compile, compile) ++= Seq("-optimize", "-feature", "-deprecation", "-unchecked", "-Xlint")

// dependencies
// versions involved in integration builds / that change frequently should be keys, set above!
libraryDependencies += "org.apache.ant"                 % "ant"            % "1.8.4" % "provided"

libraryDependencies += "com.googlecode.java-diff-utils" % "diffutils"      % "1.3.0"

libraryDependencies += "org.scala-sbt"                  % "test-interface" % "1.0"

// to run scalacheck tests, depend on scalacheck separately
libraryDependencies += "org.scalacheck"                %% "scalacheck"     % scalaCheckVersion.value % "provided"

// mark all scala dependencies as provided which means one has to explicitly provide them when depending on partest
// this allows for easy testing of modules (like scala-xml) that provide tested classes themselves and shouldn't
// pull in an older version of itself
libraryDependencies += "org.scala-lang.modules"        %% "scala-xml"      % scalaXmlVersion.value % "provided" intransitive()

libraryDependencies += "org.scala-lang"                 % "scalap"         % scalaVersion.value % "provided" intransitive()

libraryDependencies += "org.scala-lang"                 % "scala-reflect"  % scalaVersion.value % "provided" intransitive()

libraryDependencies += "org.scala-lang"                 % "scala-compiler" % scalaVersion.value % "provided" intransitive()

mimaPreviousVersion := Some("1.0.5")
