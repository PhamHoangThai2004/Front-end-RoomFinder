package com.pht.roomfinder.listen

interface OnItemClickListener {
    fun onSwitchChange(isChecked: Boolean, position: Int)
    fun onImageClick(position: Int)
    fun onTextClick(position: Int)
}