package english.master.service

class ViewCardMenuService(listSize: Int) : AbstractMenuService(listSize) {

    override fun middleButtonName(): String {
        return "Flip"
    }

    override fun middleButtonAction(): String {
        return "flip"
    }
}