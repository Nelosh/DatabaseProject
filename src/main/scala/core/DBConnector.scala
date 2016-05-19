package core


import Config._
import java.sql.{ResultSet, DriverManager}

import scala.collection.immutable.IndexedSeq

object DBConnector {
    Class.forName(driver)

    val conn = DriverManager.getConnection(url, username, password)


    def deleteFromById(table: String)(id: String): Unit = {
        val sql: String = "DELETE FROM " + table + " WHERE id = " + id
        update(sql)
    }

    def addData(table: String)(values: String*): Unit = {
        var sql: String = "INSERT INTO " + table + " VALUES (DEFAULT,"
        sql = values.foldLeft(sql)((sql, value) => sql + value + ",")
        sql = sql.dropRight(1) +  ")"
        update(sql)
    }

    def getValuesFromTable(table: String)(values: String*): Seq[Seq[String]] = {
        query("SELECT " + values.foldLeft("")((sql, value) => sql + value + ",").dropRight(1) + "FROM " + table)
    }

    def getAllFrom(table: String): Seq[Seq[String]] = {
        query("SELECT * FROM " + table)
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
