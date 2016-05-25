package core

import scala.swing._

trait LinkedEditor extends Editor{

    protected var currentLink: Seq[Label] = startingLink

    def link = {
        if (currentLink == null)
            startingLink
        else
            currentLink
    }
    def startingLink: Seq[Label] = {
        val link = Saver.load(tableName)
        if (link.isDefined)
            link.get.asInstanceOf[Seq[Label]]
        else
            getLabeledLink(getAllIDs.head)
    }

    def linkTableName: String

    def previousButtonAction(): Unit
    def nextButtonAction(): Unit

    override def formPanel = {
        Seq(
            linkSection,
            Swing.Glue,
            addSection,
            Swing.Glue,
            new BoxPanel(Orientation.Horizontal) {
                contents += addButton
                contents += Swing.HGlue
            },
            new ScrollPane(dataTable),
            backAndRemoveSection
        )
    }

    override def rawData = DBConnector.getAllFromWhere(tableName)(rawDataCondition).map(_.map((x: String) => x: Any).toArray).toArray

    def linkTableForeignKey: String = linkTableName + "id"
    def rawDataCondition: String = linkTableForeignKey + "=" + link.head.text

    def linkSection: BoxPanel = {
        new BoxPanel(Orientation.Vertical) {
            contents += new GridPanel(1, link.size){
                contents ++= link
            }
            contents += prevNextSection
        }

    }

    def prevNextSection: BoxPanel = {
        new BoxPanel(Orientation.Horizontal) {
            contents += Button("Previous") {
                previousLink()
                previousButtonAction()
            }
            contents += Swing.HStrut(100)
            contents += Button("Next") {
                nextLink()
                nextButtonAction()
            }
        }
    }

    def saveData(): Unit = {
        Saver.save(tableName, currentLink)
    }

    def previousLink(): Unit = {
        val found = getAllIDs.takeWhile(current => current != link.head.text.toInt)
        if (found.nonEmpty) {
            currentLink = getLabeledLink(found.reverse.head)
            saveData()
        }

    }

    def nextLink(): Unit = {
        val found = getAllIDs.dropWhile(current => current != link.head.text.toInt)
        if (found.tail.nonEmpty) {
            currentLink = getLabeledLink(found.tail.head)
            saveData()
        }

    }

    def getLabeledLink(id: Int): Seq[Label] = {
        getLink(id).map(x => new Label(x))
    }

    def getAllIDs: Seq[Int] = {
        DBConnector.getValuesFromTable(linkTableName)("id").flatten.map(_.toInt)
    }

    def getLink(id: Int): Seq[String] = {
        DBConnector.getByIdFrom(linkTableName)(id)
    }

}
