package com.chicagof1.scraping

trait Scraper[T] {
  def extract(html: String, url: String): T
}

