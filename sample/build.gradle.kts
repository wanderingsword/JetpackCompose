import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//插件
plugins {
	kotlin("jvm") version "1.6.10"
	id("org.jetbrains.compose") version "1.0.1-rc2"
}

//仓库
repositories {
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	google()
}


//依赖
dependencies {
	implementation(compose.desktop.currentOs)
	implementation("net.dongliu:apk-parser:2.6.10")
	implementation("com.squareup.okio:okio:3.0.0")
	implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.3")
}

//配置编译使用的 jdk 版本
tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "11"
	kotlinOptions.freeCompilerArgs += "-opt-in=org.mylibrary.OptInAnnotation"
}
//配置程序主类
compose.desktop {
	application {
		mainClass = "MainKt"
	}
}