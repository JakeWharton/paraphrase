package com.jakewharton.paraphrase

import com.android.resources.ResourceType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

class GenerateParaphraseClassesTask extends DefaultTask {
  @Input String packageName
  @InputDirectory File resDir

  @OutputDirectory File outputDir

  @TaskAction void execute(IncrementalTaskInputs inputs) {
    inputs.outOfDate { change ->
      def items = ValueResourceParser.parse(change.file)

      new ParaphraseWriter(outputDir).write(packageName, items)
    }
  }
}
