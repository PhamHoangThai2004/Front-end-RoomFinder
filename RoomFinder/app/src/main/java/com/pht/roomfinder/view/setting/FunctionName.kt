package com.pht.roomfinder.view.setting

open class FunctionName(open val name: Int, open val type: Int)

class ItemSwitch(override val name: Int, override val type: Int, val isChecked: Boolean) :
    FunctionName(name, type)

class ItemImage(override val name: Int, override val type: Int, val icon: Int) :
    FunctionName(name, type)

class ItemText(override val name: Int, override val type: Int, val text: Int) :
    FunctionName(name, type)