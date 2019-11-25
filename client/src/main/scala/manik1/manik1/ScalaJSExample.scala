package manik1.manik1

import cats.effect.IO

import outwatch.dom._
import outwatch.dom.dsl.{span => sp, _}

object HelloWoutWatch {

  val test = div(input ( id := "txtFolio" ) )

  def main(args: Array[String]): Unit = {
    val program = for {
      program <- OutWatch.renderInto[IO]("#root", test )
    } yield program
    program.unsafeRunSync()
  }
}