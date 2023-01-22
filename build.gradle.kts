import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.github.ben-manes.versions") version "0.44.0"
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Build.androidBuildTools)
        classpath(Build.kotlinGradlePlugin)
        classpath(Build.sqlDelightGradlePlugin)
        classpath(Build.hiltAndroid)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

/**
 * Task: dependencyUpdates
 *
 * Checks for dependency updates based on the set of rules set in [DependencyUpdates], favoring
 * same or more stable releases.
 */
tasks.withType<DependencyUpdatesTask> {
    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"

    val allowLessStable = project.hasProperty("allowLessStable")

    rejectVersionIf {
        if (allowLessStable) return@rejectVersionIf false

        val current = DependencyUpdates.versionToRelease(currentVersion)
        // If we're using a SNAPSHOT, ignore since we must be doing so for a reason.
        if (current == ReleaseType.SNAPSHOT) return@rejectVersionIf true

        // Otherwise we reject if the candidate is more 'unstable' than our version
        val candidate = DependencyUpdates.versionToRelease(candidate.version)
        candidate.isLessStableThan(current)
    }
}