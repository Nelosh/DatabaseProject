package core

object Saver {
    private var savedData: Map[String, Any] = Map()

    def save(dataName: String, data: Any): Unit = {
        savedData = savedData.updated(dataName, data)
    }

    def load(dataName: String): Option[Any] = savedData.get(dataName)
}
