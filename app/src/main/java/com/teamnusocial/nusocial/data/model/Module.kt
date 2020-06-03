package com.teamnusocial.nusocial.data.model

import java.time.LocalTime

data class Module(
    private val moduleCode: String,
    private val moduleName: String,
    private val classes: List<Classes>
)