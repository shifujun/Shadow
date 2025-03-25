package com.tencent.shadow.core.gradle

import com.android.SdkConstants
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import com.android.build.gradle.internal.scope.InternalArtifactType
import com.android.sdklib.AndroidVersion.VersionCodes
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import java.io.File
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

internal class AGPCompatImpl : AGPCompat {

    override fun getProcessResourcesTask(output: BaseVariantOutput): Task =
        try {
            output.processResourcesProvider.get()
        } catch (e: NoSuchMethodError) {
            output.processResources
        }

    override fun getProcessResourcesFile(processResourcesTask: Task, variantName: String): File {
        val capitalizeVariantName = variantName.capitalize()

        return try {
            File(
                processResourcesTask.outputs.files.files.first { it.name.equals("out") },
                "resources-$variantName.ap_"
            )

            // 使用 resPackageOutputFolder
            // 获取的路径和上方路径一致
            // 备选（不推荐）
            /*File(
                (processResourcesTask as LinkApplicationAndroidResourcesTask).resPackageOutputFolder.asFile.get(),
                "resources-$variantName.ap_"
            )*/
        } catch (ignored: Exception) {
            // 高版本 AGP
            try {
                // 通过反射获取 KProperty： linkedResourcesOutputDir、linkedResourcesArtifactType
                val linkedResourcesOutputDir =
                    LinkApplicationAndroidResourcesTask::class.declaredMemberProperties.first {
                        it.name == "linkedResourcesOutputDir"
                    }.let {
                        it.isAccessible = true
                        it.getter.call(processResourcesTask) as DirectoryProperty
                    }

                @Suppress("UNCHECKED_CAST")
                val linkedResourcesArtifactType =
                    LinkApplicationAndroidResourcesTask::class.declaredMemberProperties.first {
                        it.name == "linkedResourcesArtifactType"
                    }.let {
                        it.isAccessible = true
                        it.getter.call(processResourcesTask) as Property<InternalArtifactType<Directory>>
                    }

                File(
                    linkedResourcesOutputDir.asFile.get(),
                    linkedResourcesArtifactType.get().name().lowercase()
                        .replace("_", "-") + "-" + variantName + SdkConstants.DOT_RES
                )
            } catch (ignored: Exception) {
                // 反射获取出错，备用
                File(
                    processResourcesTask.outputs.files.files.first { it.name.equals("process${capitalizeVariantName}Resources") },
                    "linked-resources-binary-format-$variantName.ap_"
                )
            }
        }
    }

    @Suppress("PrivateApi")
    override fun getAaptAdditionalParameters(processResourcesTask: Task): List<String> =
        try {
            if (processResourcesTask is LinkApplicationAndroidResourcesTask) {
                processResourcesTask.aaptAdditionalParameters.get()
            } else {
                TODO("不支持的AGP版本")
            }
        } catch (ignored: NoSuchMethodError) {
            //AGP 4.0.0
            val aaptOptionsField =
                LinkApplicationAndroidResourcesTask::class.java.getDeclaredField("aaptOptions")
            aaptOptionsField.isAccessible = true
            val aaptOptions = aaptOptionsField.get(processResourcesTask)
            val additionalParametersField = try {
                aaptOptions.javaClass.getDeclaredField("additionalParameters")
            } catch (ignored: NoSuchFieldException) {
                //AGP 3.4.0
                aaptOptions.javaClass.superclass.getDeclaredField("additionalParameters")
            }

            additionalParametersField.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val additionalParameters = additionalParametersField.get(aaptOptions) as? List<String>
            additionalParameters ?: listOf()
        }

    override fun addFlavorDimension(baseExtension: BaseExtension, dimensionName: String) {
        val flavorDimensionList = baseExtension.flavorDimensionList
                as MutableList<String>? // AGP 3.6.0版本可能返回null
        if (flavorDimensionList != null) {
            flavorDimensionList.add(dimensionName)
        } else {
            baseExtension.flavorDimensions(dimensionName)
        }
    }

    override fun setProductFlavorDefault(productFlavor: ProductFlavor, isDefault: Boolean) {
        try {
            productFlavor.isDefault = isDefault
        } catch (ignored: NoSuchMethodError) {
            // AGP 3.6.0版本没有这个方法，就不设置了。
            // 设置Default主要是为了IDE中的Build Variants上下文自动选择时不要选成插件，
            // 以便在IDE直接运行插件apk模块时运行Normal版本
        }
    }

    override fun getMinSdkVersion(pluginVariant: ApplicationVariant): Int {
        // AGP在版本升级中修改了MergedFlavor的包名，但是它实现的ProductFlavor接口没有变
        val mergedFlavor = pluginVariant.mergedFlavor as com.android.builder.model.ProductFlavor
        return mergedFlavor.minSdkVersion?.apiLevel ?: VersionCodes.BASE
    }

    override fun hasDeprecatedTransformApi(): Boolean {
        try {
            val version = com.android.Version.ANDROID_GRADLE_PLUGIN_VERSION
            val majorVersion = version.substringBefore('.', "0").toInt()
            if (majorVersion >= 8) {
                return false//能parse出来主版本号大于等于8，我们就认为旧版Transform API不可用了。
            }
        } catch (ignored: Error) {
        }

        //读取版本号失败，就推测是旧版本的AGP，就应该有旧版本的Transform API
        return true
    }

    companion object {
        fun getStringFromProperty(x: Any?): String {
            return when (x) {
                is String -> x
                is Property<*> -> x.get() as String
                else -> throw Error("不支持的AGP版本")
            }
        }
    }
}
