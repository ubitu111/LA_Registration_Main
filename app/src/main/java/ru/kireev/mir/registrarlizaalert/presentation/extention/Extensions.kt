package ru.kireev.mir.registrarlizaalert.presentation.extention

import android.content.Context
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.database.GroupCallsigns

fun Enum<*>.getGroupCallsignAsString(context: Context) : String =
        when(this) {
            GroupCallsigns.LISA -> context.getString(R.string.group_callsign_lisa)
            GroupCallsigns.BORT -> context.getString(R.string.group_callsign_bort)
            GroupCallsigns.KINOLOG -> context.getString(R.string.group_callsign_kinolog)
            GroupCallsigns.VETER -> context.getString(R.string.group_callsign_veter)
            GroupCallsigns.VODA -> context.getString(R.string.group_callsign_voda)
            GroupCallsigns.PEGAS -> context.getString(R.string.group_callsign_pegas)
            GroupCallsigns.POLICE -> context.getString(R.string.group_callsign_police)
            GroupCallsigns.MCHS -> context.getString(R.string.group_callsign_mchs)
            else -> context.getString(R.string.group_callsign_pso)
        }