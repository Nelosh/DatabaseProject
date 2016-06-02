package core

import java.awt.{Shape, Graphics2D, Dimension, Font}
import javax.swing.JTable.PrintMode

import scala.swing._

class UIWindow extends MainFrame {
    title = "Window"
    preferredSize = new Dimension(1000, 700)

    var currentScreen: (() => Component) = () => homeScreen()
    contents = currentScreen()
    normalizeFont(contents.head)


    def homeScreen(): BoxPanel = {
        new BoxPanel(Orientation.Horizontal) {
            contents += new BoxPanel(Orientation.Vertical) {
                contents += new Label("Forms")
                contents += Button("Edit race table") { changeToRaceEdit() }
                contents += Button("Edit player table") { changeToPlayerEdit() }
                contents += Button("Edit fleet table") { changeToFleetEdit() }
                contents += Button("Edit ship table") { changeToShipEdit() }
                contents += Button("Edit system table") { changeToSystemEdit() }
                contents += Button("Edit planet table") { changeToPlanetEdit() }
                contents += Swing.VGlue
            }
            contents += Swing.HGlue
            contents += new BoxPanel(Orientation.Vertical) {
                contents += new Label("Queries")
                contents += Button("Race Statistics") { changeToRaceStatistics() }
                contents += Button("Find Player") { changeToFindPlayer() }
                contents += Swing.VGlue
            }
        }
    }

    def playerEditorScreen: BoxPanel = {
        new BoxPanel(Orientation.Vertical) with Editor {
            contents ++= formPanel

            def races: Seq[(String, String)] = DBConnector.getValuesFromTable("race")("id", "name").map(x => (x.head, x.tail.head))

            override def tableName = "player"

            override def additionLabels: Seq[Label] = Seq(new Label("Name"), new Label("Race"), new Label("Type"), new Label("Style"))
            override def initialComponents = Seq(
                new TextField(),
                new ComboBox[String](races.map(_._2)),
                new ComboBox[String](Seq("Live", "Bot")),
                new TextField()
            )

            override def componentValues: Map[Component, String] = {
                val c = additionalComponents
                Map(
                    keyValue(0),
                    c(1) -> races.find(_._2 == componentText(c(1))).get._1,
                    keyValue(2),
                    keyValue(3)
                )
            }

            override def addButtonAction(): Unit = refresh()
            override def removeButtonAction(): Unit = refresh()
            override def backButtonAction(): Unit = changeToHome()
        }
    }

    def raceEditorScreen: BoxPanel = {
        new BoxPanel(Orientation.Vertical) with Editor{
            contents ++= formPanel

            override def tableName: String = "race"

            override def additionLabels: Seq[Label] = Seq(new Label("Name"), new Label("Structure"), new Label("Behaviour"), new Label("Government"))
            override def initialComponents = Seq(
                new TextField,
                new ComboBox(Seq("Humanoid", "Reptiloid", "Insectoid", "Robotic")),
                new ComboBox(Seq("Aggressive", "Friendly", "Neutral")),
                new ComboBox(Seq("Democracy", "Autocracy", "Monarchy", "Anarchy")))

            override def componentValues: Map[Component, String] = (0 to 3).map(keyValue).toMap

            override def addButtonAction(): Unit = refresh()
            override def removeButtonAction(): Unit = refresh()
            override def backButtonAction(): Unit = changeToHome()

        }
    }

    def fleetEditorScreen: BoxPanel = {
        new BoxPanel(Orientation.Vertical) with LinkedEditor {
            contents ++= formPanel

            override def tableName: String = "fleet"
            override def linkTableName: String = "race"

            override def additionLabels: Seq[Label] = Seq(new Label("Race"), new Label("Experience"), new Label("Battlestyle"), new Label("Commander"), new Label("Station"))
            override def initialComponents: Seq[Component] = Seq(
                new Label(raceName),
                new TextField(),
                new TextField(),
                new TextField(),
                new TextField()
            )

            override def componentValues: Map[Component, String] = Map(additionalComponents.head -> link.head.text) ++ (1 to 4).map(keyValue)

            override def nextButtonAction(): Unit = refresh()
            override def previousButtonAction(): Unit = refresh()
            override def addButtonAction(): Unit = refresh()
            override def removeButtonAction(): Unit = refresh()
            override def backButtonAction(): Unit = changeToHome()

            def raceName = startingLink(1).text
        }
    }

    def shipEditorScreen: BoxPanel = {
        new BoxPanel(Orientation.Vertical) with LinkedEditor {
            contents ++= formPanel

            override def tableName: String = "ship"
            override def linkTableName: String = "fleet"

            override def additionLabels: Seq[Label] = Seq(new Label("Fleet Commander"), new Label("Model"), new Label("Personal"), new Label("Complectation"))
            override def initialComponents: Seq[Component] = Seq(
                new Label(fleetName),
                new TextField(),
                new TextField(),
                new ComboBox(Seq("Scout", "Battle", "Colonization", "Trade"))
            )

            override def componentValues: Map[Component, String] = Map(additionalComponents.head -> link.head.text) ++ (1 to 3).map(keyValue)

            override def nextButtonAction(): Unit = refresh()
            override def previousButtonAction(): Unit = refresh()
            override def addButtonAction(): Unit = refresh()
            override def removeButtonAction(): Unit = refresh()
            override def backButtonAction(): Unit = changeToHome()

            def fleetName = startingLink(4).text
        }
    }

    def systemEditorScreen: BoxPanel = {
        new BoxPanel(Orientation.Vertical) with LinkedEditor {
            contents ++= formPanel

            override def tableName: String = "system"
            override def linkTableName: String = "race"
            override def linkTableForeignKey = "ownerid"

            override def additionLabels: Seq[Label] = Seq(new Label("Name"), new Label("Owner"), new Label("Artefact"))
            override def initialComponents: Seq[Component] = Seq(
                new TextField(),
                new Label(raceName),
                new TextField()
            )

            override def componentValues: Map[Component, String] = Map(keyValue(0), additionalComponents(1) -> link.head.text, keyValue(2))

            override def nextButtonAction(): Unit = refresh()
            override def addButtonAction(): Unit = refresh()
            override def previousButtonAction(): Unit = refresh()
            override def removeButtonAction(): Unit = refresh()
            override def backButtonAction(): Unit = changeToHome()

            def raceName = startingLink(1).text
        }
    }

    def planetEditorScreen: BoxPanel = {
        new BoxPanel(Orientation.Vertical) with LinkedEditor {
            contents ++= formPanel

            override def tableName: String = "planet"
            override def linkTableName: String = "system"

            override def additionLabels: Seq[Label] = Seq(new Label("Name"), new Label("System"), new Label("Suface"), new Label("Size"))
            override def initialComponents: Seq[Component] = Seq(
                new TextField(),
                new Label(systemName),
                new ComboBox(Seq("Terra", "Desert", "Ice", "Tundra", "Ocean")),
                new ComboBox(Seq("Little", "Small", "Medium", "Big", "Huge"))
            )

            override def componentValues: Map[Component, String] =  Map(additionalComponents(1) -> link.head.text) ++ (2 to 3).map(keyValue) + keyValue(0)

            override def nextButtonAction(): Unit = refresh()
            override def addButtonAction(): Unit = refresh()
            override def previousButtonAction(): Unit = refresh()
            override def removeButtonAction(): Unit = refresh()
            override def backButtonAction(): Unit = changeToHome()

            def systemName = startingLink(1).text
        }
    }

    def raceStatistics: BoxPanel = {
        new BoxPanel(Orientation.Vertical) with QueryResult {
            val currentPane = new ScrollPane(initialTable)
            contents += currentPane
            contents += new BoxPanel(Orientation.Horizontal) {
                contents += Button("Print") { createPDF(currentPane.contents.head.asInstanceOf[Table]) }
                contents += Swing.HGlue
                contents += Button("Back") { changeToHome() }
            }

            override def rawData: Array[Array[Any]] = DBConnector.getGroupedByFromTables("race", "system", "fleet")("race.name", "COUNT(system.id)", "COUNT(fleet.id)")("ownerid", "raceid").map(_.map((x: String) => x: Any).toArray).toArray

            override def columnNames: Seq[String] = Seq("Race", "Systems", "Army")
        }
    }

    def findPlayers: BoxPanel = {
        new BoxPanel(Orientation.Vertical) with QueryResult {

            val currentPane = new ScrollPane(initialTable)
            val playerNameField = new TextField("") {
                maximumSize = new Dimension(Int.MaxValue, 80)
            }

            contents += new BoxPanel(Orientation.Horizontal) {
                contents += new Label("Player: ")
                contents += playerNameField
                contents += Button("Search players") { search() }
                contents += Swing.HGlue
            }
            contents += currentPane
            contents += new BoxPanel(Orientation.Horizontal) {
                contents += Button("Print") { createPDF(currentPane.contents.head.asInstanceOf[Table]) }
                contents += Swing.HGlue
                contents += Button("Back") { changeToHome() }
            }

            def search(): Unit = {
                currentPane.contents = dataTable
                normalizeFont(currentPane)
            }

            override def initialRawData: Array[Array[Any]] = DBConnector.getAllFromWhere("player")("name LIKE ''" ).map(_.map((x: String) => x: Any).toArray).toArray

            override def rawData: Array[Array[Any]] = DBConnector.getAllFromWhere("player")("name LIKE '%" + playerNameField.text + "%'" ).map(_.map((x: String) => x: Any).toArray).toArray

            override def columnNames: Seq[String] = DBConnector.getColumnNames("player").flatten.reverse
        }
    }

    def createPDF(table: Table) = {
        table.peer.print(PrintMode.FIT_WIDTH)
    }

    def getIdFromSelectedRow(table: Table): String = {
        table(table.peer.getSelectedRow, table.peer.getColumn("ID").getModelIndex).asInstanceOf[String]
    }

    def normalizeFont(component: Component): Unit = {
        val standardFont = new Font("serif", Font.PLAIN, 40)

        component match {
            case container: Container => container.contents.foreach(normalizeFont)
            case _ => component.font = standardFont
        }
    }

    def changeToRaceEdit(): Unit = changeScreen(() => raceEditorScreen)
    def changeToHome(): Unit = changeScreen(() => homeScreen())
    def changeToPlayerEdit():Unit = changeScreen(() => playerEditorScreen)
    def changeToFleetEdit(): Unit = changeScreen(() => fleetEditorScreen)
    def changeToShipEdit(): Unit = changeScreen(() => shipEditorScreen)
    def changeToSystemEdit(): Unit = changeScreen(() => systemEditorScreen)
    def changeToPlanetEdit(): Unit = changeScreen(() => planetEditorScreen)
    def changeToRaceStatistics(): Unit = changeScreen(() => raceStatistics)
    def changeToFindPlayer(): Unit = changeScreen(() => findPlayers)

    def changeScreen(f: () => Component) = {
        val currentSize = size
        currentScreen = f
        contents = f()
        normalizeFont(contents.head)
        size = currentSize
    }

    def refresh(): Unit = {
        val currentSize = size
        changeScreen(currentScreen)
        size = currentSize
    }

    override def closeOperation() = {
        super.closeOperation()
        DBConnector.closeConnection()
    }
}

