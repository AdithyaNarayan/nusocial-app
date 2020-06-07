
package com.teamnusocial.nusocial.data.model

import java.time.LocalTime

data class Module(
    val moduleCode: String,
    val moduleName: String,
    val classes: List<Classes>
)