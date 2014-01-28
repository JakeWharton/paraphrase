package com.jakewharton.paraphrase

import org.gradle.testkit.functional.GradleRunnerFactory
import org.junit.Test

import static org.junit.Assert.assertTrue

class ParaphrasePluginTest {
  @Test public void simple() {
    def runner = GradleRunnerFactory.create()
    runner.directory = new File('src/test/fixtures/simple/')
    runner.arguments << 'clean'
    runner.arguments << 'check'
    runner.arguments << '--stacktrace'

    def result = runner.run()
    assertTrue result.standardOutput.contains('BUILD SUCCESSFUL')
  }
}
