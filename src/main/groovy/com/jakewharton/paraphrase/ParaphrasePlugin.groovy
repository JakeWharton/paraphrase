package com.jakewharton.paraphrase

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class ParaphrasePlugin implements Plugin<Project> {
  @Override void apply(Project project) {
    def hasApp = project.plugins.hasPlugin AppPlugin
    def hasLib = project.plugins.hasPlugin LibraryPlugin
    if (!hasApp && !hasLib) {
      throw new IllegalStateException("'android' or 'android-library' plugin required.")
    }

    def log = project.logger
    def variants
    if (hasApp) {
      variants = project.android.applicationVariants
    } else {
      variants = project.android.libraryVariants
    }

    variants.all { variant ->
      def outDir = new File("${project.buildDir}/phrase/${variant.dirName}")
      log.debug "Paraphrase [${variant.name}] outDir: $outDir"

      def mergeTask = variant.mergeResources
      def buildConfig = variant.generateBuildConfig

      def phraseTaskName = "generate${variant.name.capitalize()}Phrase"
      def phraseTask = project.tasks.create(phraseTaskName, GenerateParaphraseClassesTask)

      phraseTask.resDir = mergeTask.outputDir
      phraseTask.outputDir = outDir

      // There is no easy way to get the package name so we steal it from the build config task.
      buildConfig.doLast {
        def packageName = buildConfig.appPackageName
        log.debug "Paraphrase [${variant.name}] packageName: $packageName"
        phraseTask.outputPackage = packageName
      }

      phraseTask.dependsOn mergeTask, buildConfig

      variant.registerJavaGeneratingTask phraseTask, outDir
    }
  }
}
