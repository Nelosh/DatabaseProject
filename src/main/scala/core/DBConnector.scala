package core


import Config._
import java.sql.{ResultSet, DriverManager}

object DBConnector {
    import Shortcuts._

    Class.forName(driver)

    val conn = DriverManager.getConnection(url, username, password)


    def formatValue(value: String): String = { "'" + value + "'" }

    def deleteFromById(table: String)(id: String): Unit = {
        val sql: String = deleteFrom + table + where + "id = " + id
        update(sql)
    }

    def addDataSeq(table: String)(data: Seq[String]): Unit = {
        val sql: String = insertInto + table + values ( valuesSplitByCommas(default +: data) )
        update(sql)
    }

    def addData(table: String)(data: String*): Unit = {
        val sql: String = insertInto + table + values ( valuesSplitByCommas(default +: data) )
        update(sql)
    }

    def getValuesFromTable(table: String)(values: String*): Seq[Seq[String]] = {
        val sql = select + valuesSplitByCommas(values) + from + table
        query(sql)
    }

    def getByIdFrom(table: String)(id: Int): Seq[String] = {
        query(select + all + from + table + where + "id=" + id).flatten
    }

    def getGroupedByFromTables(tables: String*)(mainColumn: String, countColumns: String*)(foreignKeys: String*) = {
        val sql = select + valuesSplitByCommas(Seq(mainColumn) ++ countColumns) + from + innerJoinedTables(tables, foreignKeys) + groupBy + mainColumn
        print(sql)
        query(sql)
    }

    def getAllFrom(table: String): Seq[Seq[String]] = {
        query(select + all + from + table)
    }

    def getAllFromWhere(table: String)(condition: String): Seq[Seq[String]] = {
        query(select + all + from + table + where + condition)
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

    def getColumnNames(table: String): Seq[Seq[String]] = {
        val sql = select + "column_name" + from + "information_schema.columns" + where + "table_name=" + "'"+table+"'"
        query(sql)
    }

    def valuesSplitByCommas(values: Seq[String]): String = {
        values.foldLeft("")((sql, value) => sql + value + ",").dropRight(1)
    }

    def innerJoinedTables(tables: Seq[String], foreignKeys: Seq[String]): String = {
        val firstTable = tables.head
        tables.tail.foldLeft((firstTable, firstTable, foreignKeys))((left, right) => (left._1 + innerJoin + right + on + left._2 + ".id=" + right + "." + left._3.head, left._2, left._3.tail))._1
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
    val innerJoin = " INNER JOIN "
    val groupBy = " GROUP BY "
    val on = " ON "
    val all = "*"
    val default = "DEFAULT"

    def values(values: String): String = " VALUES (" + values + ")"
}
