package com.zipper.fetch.cookie.ui.minimt.model

import androidx.annotation.IntDef


@IntDef(value = [MiniProgramType.ZYCS, MiniProgramType.KGLG])
@Retention(value = AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class MiniProgramType {

    companion object {
        const val ZYCS = 1
        const val KGLG = 2
    }
}
