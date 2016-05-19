package core

import Config._
import java.sql.{Statement, DriverManager, Connection}

import scala.util.Try

object Main extends {


    def main(args: Array[String]) = {
        val ui = new UIWindow
        ui.visible = true
    }

}
