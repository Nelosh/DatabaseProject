package core


import java.awt.Font

import scala.swing._

trait Editor {

    private val currentAdditionComponents: Seq[Component] = initialComponents
    private val currentTable = dataTable

    def tableName: String

    def additionLabels: Seq[Label]

    def componentValues: Map[Component, String]
    def initialComponents: Seq[Component]

    def addButtonAction(): Unit
    def removeButtonAction(): Unit
    def backButtonAction(): Unit

    def additionalComponents: Seq[Component] = currentAdditionComponents

    def formPanel: Seq[Component] = {
        Seq(
            addSection,
            Swing.Glue,
            new BoxPanel(Orientation.Horizontal) {
                contents += addButton
                contents += Swing.HGlue
            },
            new ScrollPane(currentTable),
            backAndRemoveSection
        )
    }


    def addSection: GridPanel = {
        new GridPanel(2, additionLabels.size) {
            contents ++= additionLabels
            contents ++= additionalComponents
        }
    }

    def backAndRemoveSection: BoxPanel = {
        new BoxPanel(Orientation.Horizontal) {
            contents += removeButton
            contents += Swing.HGlue
            contents += Button("Back") { backButtonAction() }
        }
    }

    def columnNames = DBConnector.getColumnNames(tableName).flatten.reverse
    def rawData = DBConnector.getAllFrom(tableName).map(_.map((x: String) => x: Any).toArray).toArray

    def dataTable = new Table(rawData, columnNames) {
        selection.elementMode = Table.ElementMode.Row
        peer.getTableHeader.setFont(new swing.Font("serif", Font.PLAIN, 50))
        peer.setRowHeight(70)
    }

    def addButton: Button = Button("Add") {
        import DBConnector.formatValue
        if (additionalComponents.forall(componentValues(_) != "")) {
            DBConnector.addDataSeq(tableName)(additionalComponents.map(x => formatValue(componentValues(x))))
            addButtonAction()
        }
    }

    def removeButton = Button("Remove") {
        val id = getIdFromSelectedRow(currentTable)
        if (id.isDefined) {
            print(id.get)
            DBConnector.deleteFromById(tableName)(id.get)
            removeButtonAction()
        }
    }

    def componentText(component: Component): String = {
        component match {
            case l: Label => l.text
            case c: ComboBox[String] => c.selection.item
            case t: TextComponent => t.text
            case _ => "not found"
        }
    }

    def keyValue(i: Int): (Component, String) = additionalComponents(i) -> componentText(additionalComponents(i))

    private def getIdFromSelectedRow(table: Table): Option[String] = {
        if (table.selection.rows.size == 1)
            Some(table(table.peer.getSelectedRow, table.peer.getColumn("id").getModelIndex).asInstanceOf[String])
        else
            None
    }

}
