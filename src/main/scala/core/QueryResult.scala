package core

import java.awt.Font

import scala.swing.Table

trait QueryResult {


    def rawData: Array[Array[Any]]
    def columnNames: Seq[String]
    def dataTable = new Table(rawData, columnNames) {
        selection.elementMode = Table.ElementMode.None
        peer.getTableHeader.setFont(new swing.Font("serif", Font.PLAIN, 50))
        peer.setRowHeight(70)
    }

}
