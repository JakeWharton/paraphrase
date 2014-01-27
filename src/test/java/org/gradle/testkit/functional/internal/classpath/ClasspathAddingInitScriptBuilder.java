package org.gradle.testkit.functional.internal.classpath;

import org.gradle.internal.ErroringAction;
import org.gradle.internal.IoActions;
import org.gradle.util.TextUtil;

import java.io.File;
import java.io.Writer;
import java.util.List;

public class ClasspathAddingInitScriptBuilder {
  public void build(File initScriptFile, final List<File> classpath) {
    IoActions.writeTextFile(initScriptFile, new ErroringAction<Writer>() {
      @Override
      protected void doExecute(Writer writer) throws Exception {
        writer.write("allprojects {\n");
        writer.write("  buildscript {\n");
        writer.write("    dependencies {\n");
        writer.write("      classpath files(\n");
        int i = 0;
        for (File file : classpath) {
          writer.write(
              String.format("        '%s'", TextUtil.escapeString(file.getAbsolutePath())));
          if (++i != classpath.size()) {
            writer.write(",\n");
          }
        }
        writer.write("\n");
        writer.write("      )\n");
        writer.write("    }\n");
        writer.write("  }\n");
        writer.write("}\n");
      }
    });
  }
}
