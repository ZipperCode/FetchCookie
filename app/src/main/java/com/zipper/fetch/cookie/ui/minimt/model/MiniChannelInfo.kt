package com.zipper.fetch.cookie.ui.minimt.model

data class MiniChannelInfo(
    val id: Int,
    val name: String,
    val startTime: Long,
    val endTime: Long,
    val sysCurrentTime: Long,
    val appointStartTime: Long,
    val appointEndTime: Long,
    val drawTime: Long,
    val purchaseStartTime: Long,
    val purchaseEndTime: Long,
    val appointCounts: Int,
    val isAppoint: Int
)
