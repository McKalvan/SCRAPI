package com.github.mckalvan.scrapi.models.mixins

import com.github.mckalvan.scrapi.utils.RedditConstants.{JSON, DATA, CHILDREN, THINGS}
import org.json4s.JsonAST.{JField, JNothing}
import org.json4s.native.JsonMethods.{parse, pretty, render}
import org.json4s.{DefaultFormats, JArray, JValue}

/*
 * Object used to parse/handle JSON body and header returned after making a call to Reddit's API
 */
object ResponseHandler {

  abstract class ResponseObj {
    def toString: String
    def toBool: Boolean
    def toInt: Int
    def extract[T: Manifest]: T
    def prettyPrint: String
    def children: List[ResponseObj]
    def getField( k : String ) : ResponseObj
    def \(k: String): ResponseObj = getField(k)
    def remove(k: String): ResponseObj
    def fieldExists(k: String): Boolean
    def arrayIsEmpty(k: String): Boolean
  }

  abstract class HeaderObj {
    def contains(k: String): Boolean
    def getHeader[A](k: String): Option[A]
  }

  case class JsonResponseObj(json: JValue) extends ResponseObj {
    implicit val formats: DefaultFormats.type = DefaultFormats

    override def toString: String = json.extractOrElse[String]("")

    def toInt: Int = json.extractOrElse[Int](0)

    def toBool: Boolean = json.extractOrElse[Boolean](false)

    def extract[T : Manifest] : T = json.extract[T]

    def prettyPrint: String = pretty(render(json))

    def children : List[ResponseObj] = json.children.map { x : JValue => JsonResponseObj(x) }

    def getField(k: String): ResponseObj = JsonResponseObj(json \ k)

    def remove(k: String): ResponseObj = {
      val updatedJSON = json removeField {
        case JField(fieldName, _) if fieldName == k => true
        case _ => false
      }
      JsonResponseObj(updatedJSON)
    }

    def fieldExists(k: String ): Boolean = {
      json \ k match {
        case a: JValue => if (a != JNothing) true else false
        case b: JArray => if (b.arr.nonEmpty) true else false
      }
    }

    def arrayIsEmpty(k: String): Boolean = {
      json \ k match  {
        case a: JArray => a.arr.isEmpty
        case _ => false
      }
    }
  }

  object JsonResponseObj {
    def apply(json: String): JsonResponseObj = JsonResponseObj(parse(json))
  }

  case class ParsedHeaderObj(headers:  Map[String,IndexedSeq[String]]) extends HeaderObj {
    def contains(k: String): Boolean = headers.contains(k)
    def getHeader[A](k: String): Option[A] = headers.get(k).asInstanceOf[Option[A]]
  }

  object ParsedHeaderObj {
    def parseHeader(headers: Map[String,IndexedSeq[String]]): HeaderObj = ParsedHeaderObj(headers)
  }

  // Trait encapsulating the functionality of the above classes to allow implementing classes to parse responses
  trait ResponseHandler {
    type ResponseObj = ResponseHandler.ResponseObj
    type HeaderValue = ResponseHandler.HeaderObj

    protected def parseBody(json: String): ResponseObj = JsonResponseObj(json)
    protected def parseHeaders(headers: Map[String,IndexedSeq[String]]): HeaderValue = ParsedHeaderObj(headers)

    implicit def jsonValueToString(json: ResponseObj): String = json.toString
    implicit def jsonValueToInt(json: ResponseObj): Int = json.toInt
    implicit def jsonValueToBool(json: ResponseObj): Boolean = json.toBool

    // TODO: Maybe move this functions into their own file?
    type JsonParser[A] = ResponseObj => A
    protected def parseNothing: JsonParser[ResponseObj] = (json: ResponseObj) => json
    protected def parseJDT: JsonParser[ResponseObj] = (json: ResponseObj) => json \ JSON \  DATA \ THINGS
    protected def parseD: JsonParser[ResponseObj] = (json: ResponseObj) => json \ DATA
    protected def parseDC: JsonParser[ResponseObj] = (json: ResponseObj) => parseD(json) \ CHILDREN
    protected def parseDCD: JsonParser[ResponseObj] = (json: ResponseObj) => (parseD(json) \ CHILDREN) \ DATA

    protected def extractNothing[A: Manifest]: JsonParser[A] = (json: ResponseObj) => json.extract[A]
    protected def extractD[A: Manifest]: JsonParser[A] = (json: ResponseObj) => parseD(json).extract[A]
    protected def extractDC[A: Manifest]: JsonParser[A] = (json: ResponseObj) => parseDC(json).extract[A]
    protected def extractHead[A: Manifest]: JsonParser[A] = (json: ResponseObj) => parseDCD(json).children.head.extract[A]
  }
}
