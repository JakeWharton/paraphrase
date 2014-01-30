package com.jakewharton.paraphrase

import org.gradle.testkit.functional.GradleRunnerFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static org.junit.Assert.assertTrue

@RunWith(Parameterized)
class FixtureIntegrationTest {
  @Parameterized.Parameters(name = "{0}")
  public static Iterable<Object[]> data() {
    return [
        // Just a simple string with a few tokens. The common base case usage.
        'simple'
    ].collect { [ it ] as Object[] }
  }

  @Parameterized.Parameter
  public String name;

  @Test public void fixtureCompiles() {
    def fixture = new File("src/test/fixtures/$name/")
    assertTrue "Fixture '$fixture' does not exist.", fixture.exists()
    assertTrue "Fixture '$fixture' is not a directory.", fixture.isDirectory()

    def runner = GradleRunnerFactory.create()
    runner.directory = fixture
    runner.arguments.addAll 'clean', 'check', '--stacktrace'

    def result = runner.run()
    assertTrue result.standardOutput.contains('BUILD SUCCESSFUL')
  }
}
