/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.testkit.functional.internal;

import org.gradle.testkit.functional.ExecutionResult;
import org.gradle.testkit.functional.GradleRunner;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class DefaultGradleRunner implements GradleRunner {
  private File directory;
  private List<String> arguments = new LinkedList<String>();

  private final GradleHandleFactory handleFactory;

  public DefaultGradleRunner(GradleHandleFactory handleFactory) {
    this.handleFactory = handleFactory;
  }

  public File getDirectory() {
    return directory;
  }

  public void setDirectory(File directory) {
    this.directory = directory;
  }

  public List<String> getArguments() {
    return arguments;
  }

  public void setArguments(List<String> arguments) {
    this.arguments = arguments;
  }

  public ExecutionResult run() {
    return start().waitForFinish();
  }

  private GradleHandle start() {
    return handleFactory.start(directory, arguments);
  }
}
