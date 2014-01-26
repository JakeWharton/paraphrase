package com.jakewharton.paraphrase

import com.android.build.gradle.LibraryPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertNotNull

class ParaphrasePluginTest {
  @Test public void simple() {
    def project = ProjectBuilder.builder()
        .withProjectDir(new File('src/test/fixtures/simple/'))
        .withName('simple')
        .build()

    project.apply plugin: LibraryPlugin
    project.apply plugin: ParaphrasePlugin

    project.evaluate() // Force evaluation of the project.

    assertNotNull project.tasks.getByName('generateDebugPhrase')
    assertNotNull project.tasks.getByName('generateReleasePhrase')
  }
}
