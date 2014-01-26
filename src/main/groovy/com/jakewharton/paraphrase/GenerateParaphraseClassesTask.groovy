package com.jakewharton.paraphrase

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

class GenerateParaphraseClassesTask extends DefaultTask {
  @OutputDirectory File outputDir
  @InputDirectory File resDir

  @TaskAction void execute(IncrementalTaskInputs inputs) {
    inputs.outOfDate { change ->
      // TODO scan for all xml files
      // TODO scan for all strings in xml files
      // TODO regex match strings with phrase tags
      // TODO generate files for each
    }

    inputs.removed { change ->
      // Nothing to do here...
    }
  }
}
