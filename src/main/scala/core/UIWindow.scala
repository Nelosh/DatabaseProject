package core

import java.awt.{Dimension, Font}

import scala.swing._

class UIWindow extends MainFrame {
    title = "Window"
    preferredSize = new Dimension(1000, 500)

    val homePanel = homeScreen
    val raceEditor = raceEditorScreen
    val playerEditor = new BoxPanel(Orientation.Vertical) {

        val nameField = new TextField()

        val races: Seq[(String, String)] = DBConnector.getValuesFromTable("player")("id", "name").map(x => (x.head, x.tail.head))



        contents += new GridPanel(2, 4) {
            contents ++= Seq(new Label("Name"), new Label("Race"), new Label("Type"), new Label("Style"))
            contents ++= Seq(new TextField(), new TextField(), new TextField(), new TextField())
        }

    }

    contents = homePanel

    normalizeFont(contents.head)

    def homeScreen: BoxPanel = {
        new BoxPanel(Orientation.Vertical) {
            contents += new Label("Here be dragons")
            contents += Button("Edit race table") {
                changeToRaceEdit()
            }
            contents += Button("Magic button") {
                changeToRaceEdit()
            }
        }
    }

    def raceEditorScreen: BoxPanel = {
        new BoxPanel(Orientation.Vertical) {

            val structures = Seq("Humanoid", "Reptiloid", "Insectoid", "Robotic")
            val behaviours = Seq("Aggressive", "Friendly", "Neutral")
            val governments = Seq("Democracy", "Autocracy", "Monarchy", "Anarchy")

            val nameField = new TextField
            val structureCombobox = new ComboBox(structures)
            val behaviourField = new ComboBox(behaviours)
            val governmentField = new ComboBox(governments)

            val convertedArray = DBConnector.getAllFrom("race").map(_.map((x: String) => x: Any).toArray).toArray

            val dataTable = new Table(convertedArray, Seq("ID", "Name", "Structure", "Behaviour", "Government")) {
                selection.elementMode = Table.ElementMode.Row
                peer.getTableHeader.setFont(new swing.Font("serif", Font.PLAIN, 50))
                peer.setRowHeight(70)
            }

            contents += screenAddSection
            contents += Swing.Glue
            contents += new BoxPanel(Orientation.Horizontal) {
                contents += addButton
                contents += Swing.HGlue
            }

            contents += new ScrollPane(dataTable)
            contents += new BoxPanel(Orientation.Horizontal) {
                contents += Button("Remove") {
                    DBConnector.deleteFromById("race")(getIdFromSelectedRow(dataTable))
                }
                contents += Swing.HGlue
                contents += Button("Back") {
                    changeToHome()
                }
            }

            private def screenAddSection: GridPanel = {
                new GridPanel(2, 4) {
                    contents ++= Seq(new Label("Name"), new Label("Structure"), new Label("Behaviour"), new Label("Government"))
                    contents ++= Seq(nameField, structureCombobox, behaviourField, governmentField)
                }
            }

            private def addButton: Button = {
                Button("Add") {
                    DBConnector.addData("race")(
                        "'" + nameField.text + "'",
                        "'" + structureCombobox.selection.item + "'",
                        "'" + behaviourField.selection.item + "'",
                        "'" + governmentField.selection.item + "'")
                }
            }
        }
    }

    def getIdFromSelectedRow(table: Table): String = {
        table(table.peer.getSelectedRow, table.peer.getColumn("ID").getModelIndex).asInstanceOf[String]
    }

    def normalizeFont(component: Component): Unit = {
        val standartFont = new Font("serif", Font.PLAIN, 50)

        component match {
            case container: Container => container.contents.foreach(normalizeFont)
            case _ => component.font = standartFont
        }
    }

    def changeToRaceEdit(): Unit = {
        contents = raceEditor
        normalizeFont(contents.head)
    }

    def changeToHome(): Unit = {
        contents = homePanel
        normalizeFont(contents.head)
    }

    override def closeOperation() = {
        super.closeOperation()
        DBConnector.closeConnection()
    }
}

