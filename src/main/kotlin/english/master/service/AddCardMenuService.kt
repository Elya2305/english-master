package english.master.service

class AddCardMenuService(listSize: Int): AbstractMenuService(listSize) {

    override fun middleButtonName(): String {
        return "Add/Remove"
    }

    override fun middleButtonAction(): String {
       return "add"
    }
}