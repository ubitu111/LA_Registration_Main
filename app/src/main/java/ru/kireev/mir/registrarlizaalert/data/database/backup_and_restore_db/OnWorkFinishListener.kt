package ru.kireev.mir.registrarlizaalert.data.database.backup_and_restore_db

interface OnWorkFinishListener {
    fun onFinished(success: Boolean, message: String)
}