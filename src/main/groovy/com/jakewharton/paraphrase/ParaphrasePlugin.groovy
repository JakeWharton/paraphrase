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
      def name = variant.buildType.name
      def outDir = new File("${project.buildDir}/phrase/${name}")
      variant.addJavaSourceFoldersToModel outDir
      log.debug "Paraphrase [$name] outDir: $outDir"

      def phraseTaskName = "generate${name.capitalize()}Phrase"
      def phraseTask = project.tasks.create(phraseTaskName, GenerateParaphraseClassesTask)

      def mergeTask = variant.mergeResources
      def compileTask = variant.javaCompile

      phraseTask.resDir = mergeTask.outputDir
      phraseTask.outputDir = outDir

      phraseTask.dependsOn mergeTask
      compileTask.dependsOn phraseTask
    }
  }
}
