// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.ksp) apply false
}

val ktlint: Configuration by configurations.creating
val detekt: Configuration by configurations.creating

dependencies {
    ktlint(libs.ktlint.cli) {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
    detekt(libs.detekt.cli)
}

val kotlinFiles = listOf("app/src/**/*.kt", "**/*.kts", "!**/build/**")

tasks.register<JavaExec>("ktlintCheck") {
    group = "verification"
    description = "Проверява стила на Kotlin кода"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args(kotlinFiles)
}

tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Поправя автоматично стила на Kotlin кода"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    args(listOf("-F") + kotlinFiles)
}

tasks.register<JavaExec>("detekt") {
    group = "verification"
    description = "Статичен анализ на Kotlin кода"
    classpath = detekt
    mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
    args(
        "--input",
        "app/src/main/java,app/src/test/java,app/src/androidTest/java",
    )
}
