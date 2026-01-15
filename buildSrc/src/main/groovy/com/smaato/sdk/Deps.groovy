package com.smaato.sdk

import com.android.SdkConstants
import com.android.Version
import groovy.transform.CompileStatic

@CompileStatic
final class Deps {
    static final class versions {
        static final String agp = Version.ANDROID_GRADLE_PLUGIN_VERSION
        static final String checkstyle = "8.24"

        static final class ax {
            static final String annotation = "1.2.0"
        }
    }

    static final String agp = "${SdkConstants.GRADLE_PLUGIN_NAME}${versions.agp}"

    static final class ax {
        static final String annotation = "androidx.annotation:annotation:${versions.ax.annotation}"
    }

}