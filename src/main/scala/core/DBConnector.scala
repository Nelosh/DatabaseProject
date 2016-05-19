package core


import Config._
import java.sql.{ResultSet, DriverManager}

import scala.collection.immutable.IndexedSeq

object DBConnector {
    import Shortcuts._

    Class.forName(driver)

    val conn = DriverManager.getConnection(url, username, password)


    def deleteFromById(table: String)(id: String): Unit = {
        val sql: String = deleteFrom + table + where + "id = " + id
        update(sql)
    }

    def addData(table: String)(data: String*): Unit = {
        val sql: String = insertInto + table + values ( valuesSplitedByCommas(default +: data) )
        update(sql)
    }

    def getValuesFromTable(table: String)(values: String*): Seq[Seq[String]] = {
        val sql = select + valuesSplitedByCommas(values) + from + table
        query(sql)
    }

    def valuesSplitedByCommas(values: Seq[String]): String = {
        values.foldLeft("")((sql, value) => sql + value + ",").dropRight(1)
    }

    def getAllFrom(table: String): Seq[Seq[String]] = {
        query(select + all + from + table)
    }

    def query(sql: String): Seq[Seq[String]] = {
        val statement = conn.createStatement()
        val resultSet: ResultSet = statement.executeQuery(sql)
        statement.closeOnCompletion()

        getRowsFromResultSet(resultSet)
    }

    def getRowsFromResultSet(resultSet: ResultSet) = {
        val rows = resultSet.getMetaData.getColumnCount
        var result = Seq[Seq[String]]()
        while (resultSet.next()) {
            result +:= (1 to rows).map(resultSet.getString)
        }
        result
    }

    def update(sql: String): Unit = {
        val statement = conn.createStatement()
        statement.executeUpdate(sql)
        statement.closeOnCompletion()
    }

    def closeConnection(): Unit = {
        conn.close()
    }
}

object Shortcuts {
    val select = "SELECT "
    val from = " FROM "
    val where = " WHERE "
    val deleteFrom = "DELETE FROM "
    val insertInto = "INSERT INTO "
    val all = "*"
    val default = "DEFAULT"

    def values(values: String): String = " VALUES (" + values + ")"
}
