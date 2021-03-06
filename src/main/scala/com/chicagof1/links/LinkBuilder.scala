package com.chicagof1.links

import com.chicagof1.model._

object LinkBuilder {
  def racerLink(name: String, racer: Option[SingleRacer]): String = {
    racer match {
      case Some(r) =>
        val flag = flagImg(r.flag)
        s"$flag$space<a href='/racers/${r.id}' class='racerLink'>$name</a>"
      case None =>
        val flag = flagImg("USA")
        s"$flag$space<span class='racerNoLink'>$name</span>"
    }
  }

  def editionLink(e: EditionInChampionship): String = {
    e match {
      case r: ReportedEditionInChampionship => s"<a class='standingEditionLink' href='/editions/${r.edition.date}'>${e.name}</a>"
      case nr: NonReportedEditionInChampionship => s"<span>${e.name}</span>"
    }
  }

  val space = "<span> </span>"

  private def flagImg(nationality: String): String = {
    val nationCode = flagCodeByNationality.getOrElse(nationality, "us")
    s"""<img src="/images/blank.gif" class="flag flag-$nationCode" alt="Czech Republic" />"""
  }

  private val flagCodeByNationality: Map[String, String] = Map(
    "USA" -> "us",
    "GBR" -> "gb",
    "ITA" -> "it",
    "LIT" -> "lt",
    "CHI" -> "cn",
    "MEX" -> "mx",
    "MAL" -> "my",
    "GER" -> "de",
    "FRA" -> "fr",
    "KOR" -> "kr",
    "RUS" -> "ru",
    "PRC" -> "pr",
    "POL" -> "pl",
    "ROM" -> "ro",
    "IND" -> "in",
    "AZE" -> "az",
    "GRE" -> "gr",
    "NIG" -> "ng",
    "SWE" -> "se"
  )
}
