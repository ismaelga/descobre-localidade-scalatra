package com.ismaelabreu.descobrelocalidade

import org.scalatra._
import scalate.ScalateSupport
import scala.util.parsing.json.JSON
import scala.io.Source
import java.util.Date
import java.util.regex.Pattern
import java.text.Normalizer

object StringNormalizer {
  def normalizeSymbolsAndAccents(str: String): String = {
    //    str = org.apache.commons.lang.StringUtils.defaultString(str)
    val nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    pattern.matcher(nfdNormalizedString).replaceAll("")
  }
}

object Locations {
  val path = "./src/main/resources/localidades.json"
  val locationsSource = JSON.parseFull(Source.fromFile("./src/main/resources/localidades.json").mkString)
  var locations: Vector[String] = Vector()
  var normalizedLocations: Vector[String] = Vector()

  private def processLocations(): Unit = {
    def process(l: Vector[String]) = {
      this.locations = l.sorted.distinct
      this.normalizedLocations = locations.map{
        StringNormalizer.normalizeSymbolsAndAccents(_)
      }.map {
        _.replaceAll(" ","").toLowerCase.sorted
      }
    }

    locationsSource match {
      case Some(x) => process(x.asInstanceOf[List[String]].toVector)
      case _ => println("Oops")
    }
  }

  processLocations()
}

class Search(val params: (String) => String) {
  def locations = Locations.locations
  def normalizedLocations = Locations.normalizedLocations

  val letters = StringNormalizer.normalizeSymbolsAndAccents(params("chars"))
  val normalizedLetters = letters.replaceAll(" ","").toLowerCase.sorted
  val words = params("words").toInt

  val all = try {
    params("all")
  } catch  {
    case e: Throwable => "NO"
  }

  def runFullLettersMatch(): Vector[String] = {
    val indexes = for {
      (l, i) <- normalizedLocations.zipWithIndex
      if l == normalizedLetters
    } yield i

    indexes.map(locations(_))
  }

  def runPartialLettersMatch(): Vector[String] = {
    Vector("partial")
  }

  private def run(): Vector[String] = {
    if(letters.isEmpty) return Vector("add some letters")

    all match {
      case "YES"  => runFullLettersMatch()
      case "NO"   => runPartialLettersMatch()
      case _ => Vector("add some params")
    }

  }

  lazy val results = run()
}


class DescobreLocalidadeServlet extends DescobreLocalidadeScalatraStack {

  get("/") {
    <html>
      <body>
        <form action="search">
          Letras: <input name="chars"/>
          Numero de letras:
            <select name="words">
              <option>1</option>
              <option>2</option>
              <option>3</option>
              <option>4</option>
              <option>5</option>
              <option>6</option>
            </select>
          Sao todas: <input type="checkbox" name="all" value="YES" />
          <input type="submit"/>
        </form>
      </body>
    </html>
  }

  get("/search") {
    val search2 = new Search(params)
    val start = new Date
    val res = search2.results
    val stop = new Date
    println((stop.getTime.toDouble - start.getTime.toDouble)/ 1000)
    res
  }

}
