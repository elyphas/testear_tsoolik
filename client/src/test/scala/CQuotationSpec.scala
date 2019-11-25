package manik1.modules.generateorders

import scala.concurrent.ExecutionContext
import cats.effect.IO

import monix.reactive.subjects.PublishSubject
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import outwatch.dom._
import outwatch.dom.dsl._
import outwatch.ext.monix._
import outwatch.ext.monix.handler._

import cats.effect.{SyncIO, IO}

class CQuotationSpec extends FlatSpec with Matchers with ScalaFutures {

  "txt in frmComparative" should "contain a string" in {

    val testear = div(input(id:="txtFolio"))

    for {
      _ <- OutWatch.renderInto[IO]( "#root", testear )
      _ <- IO(println("2  ********************************************"))
      txtFolio <- IO(org.scalajs.dom.document.getElementById("txtFolio"))
      _ <- IO(println("3  ********************************************"))
      _ <- IO(txtFolio.textContent shouldBe "")
      _ <- IO(println("4  ********************************************"))
    } yield succeed
  }

}