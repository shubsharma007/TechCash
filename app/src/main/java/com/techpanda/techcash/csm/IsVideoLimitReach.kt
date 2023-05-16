package com.techpanda.techcash.csm

interface IsVideoLimitReach {
    fun onVideoLimitReach(videoLimitReach: Boolean, isError: Boolean)
}