package com.jakewharton.paraphrase;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Closeables;
import com.squareup.javawriter.JavaWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

final class ParaphraseWriter {
  private static final String PHRASE_CLASS = "Phrase";
  private static final String ABSTRACT_PHRASE_CLASS = "AbstractPhrase";

  private final File outputDir;

  ParaphraseWriter(File outputDir) {
    this.outputDir = outputDir;
  }

  void write(String packageName, List<Phrase> phrases) throws IOException {
    String filePath = packageName.replace('.', File.separatorChar) + File.separator + PHRASE_CLASS + ".java";

    File file = new File(outputDir, filePath);
    file.getParentFile().mkdirs();

    FileWriter fileWriter = new FileWriter(file);
    JavaWriter writer = new JavaWriter(fileWriter);
    try {
      writer.emitSingleLineComment("Generated by Paraphrase plugin. Do not modify!");
      writer.emitPackage(packageName);

      writer.emitImports("android.content.Context");
      writer.emitImports("android.content.res.Resources");
      writer.emitImports("android.text.SpannableStringBuilder");
      writer.emitImports("android.view.View");
      writer.emitImports("java.util.Arrays");
      writer.emitEmptyLine();

      writer.beginType(PHRASE_CLASS, "class", EnumSet.of(PUBLIC, FINAL));

      // Factory method for each phrase.
      for (Phrase phrase : phrases) {
        String className = classNameOf(phrase);

        if (phrase.documentation != null) {
          writer.emitJavadoc(phrase.documentation);
        }
        writer.beginMethod(className, phrase.name, EnumSet.of(PUBLIC, STATIC));
        writer.emitStatement("return new %s()", className);
        writer.endMethod();
        writer.emitEmptyLine();
      }

      for (Phrase phrase : phrases) {
        writePhraseClass(writer, phrase);
        writer.emitEmptyLine();
      }

      writeAbstractPhraseClass(writer);
      writer.emitEmptyLine();

      writer.beginConstructor(EnumSet.of(PRIVATE));
      writer.emitStatement("throw new AssertionError(\"No instances.\")");
      writer.endConstructor();

      writer.endType();
    } finally {
      Closeables.close(writer, true);
    }
  }

  static void writePhraseClass(JavaWriter writer, Phrase phrase) throws IOException {
    String className = classNameOf(phrase);
    writer.beginType(className, "class", EnumSet.of(PUBLIC, STATIC, FINAL), ABSTRACT_PHRASE_CLASS);

    // Empty, no-arg constructor.
    writer.beginConstructor(EnumSet.of(PRIVATE));
    writer.emitStatement("super(R.string.%s, %s)", phrase.name, Joiner.on(", ")
        .join(FluentIterable.from(phrase.tokens).transform(new Function<String, String>() {
          @Override public String apply(String token) {
            return "\"" + token + "\"";
          }
        })));
    writer.endConstructor();
    writer.emitEmptyLine();

    List<String> tokens = phrase.tokens;
    for (int i = 0, count = tokens.size(); i < count; i++) {
      String tokenName = tokens.get(i);
      writer.beginMethod(className, tokenName, EnumSet.of(PUBLIC), "CharSequence", tokenName);
      writer.emitStatement("values[%s] = %s", i, tokenName);
      writer.emitStatement("return this");
      writer.endMethod();

      if (i < count - 1) {
        writer.emitEmptyLine();
      }
    }

    writer.endType();
  }

  private static void writeAbstractPhraseClass(JavaWriter writer) throws IOException {
    writer.beginType(ABSTRACT_PHRASE_CLASS, "class", EnumSet.of(PUBLIC, STATIC, ABSTRACT));

    writer.emitField("int", "resId", EnumSet.of(PRIVATE, FINAL));
    writer.emitField("String[]", "keys", EnumSet.of(PRIVATE, FINAL));
    writer.emitField("CharSequence[]", "values", EnumSet.of(FINAL));
    writer.emitEmptyLine();

    writer.beginConstructor(EnumSet.of(PRIVATE), "int", "resId", "String...", "keys");
    writer.emitStatement("this.resId = resId");
    writer.emitStatement("this.keys = keys");
    writer.emitStatement("this.values = new String[keys.length]");
    writer.endConstructor();
    writer.emitEmptyLine();

    writer.emitAnnotation(SuppressWarnings.class, "\"ConstantConditions\"");
    writer.beginMethod("CharSequence", "build", EnumSet.of(PUBLIC, FINAL), "View", "view");
    writer.emitStatement("return build(view.getContext().getResources())");
    writer.endMethod();
    writer.emitEmptyLine();

    writer.beginMethod("CharSequence", "build", EnumSet.of(PUBLIC, FINAL), "Context", "context");
    writer.emitStatement("return build(context.getResources())");
    writer.endMethod();
    writer.emitEmptyLine();

    writer.beginMethod("CharSequence", "build", EnumSet.of(PUBLIC, FINAL), "Resources", "res");
    writer.emitStatement(
        "SpannableStringBuilder text = new SpannableStringBuilder(res.getText(resId))");
    writer.emitStatement("int token = 0");
    writer.beginControlFlow("while ((token = nextToken(text, token)) != -1)");
    writer.emitStatement("String key = tokenAt(text, token)");
    writer.beginControlFlow("if (key == null)");
    writer.emitSingleLineComment("Skip this and next character to avoid '{{' instances.");
    writer.emitStatement("token += 2");
    writer.emitStatement("continue");
    writer.endControlFlow();
    writer.emitStatement("int index = Arrays.binarySearch(keys, key)");
    writer.beginControlFlow("if (index < 0)");
    writer.emitStatement(
        "throw new AssertionError(\"Unknown key '\" + key + \"' in: \" + Arrays.toString(keys))");
    writer.endControlFlow();
    writer.emitStatement("CharSequence value = values[index]");
    writer.emitStatement("text.replace(token, token + key.length() + 2, value)");
    writer.emitStatement("token += value.length()");
    writer.endControlFlow();
    writer.emitStatement("return text");
    writer.endMethod();
    writer.emitEmptyLine();

    writer.beginMethod("int", "nextToken", EnumSet.of(PRIVATE, STATIC), "SpannableStringBuilder",
        "b", "int", "start");
    writer.beginControlFlow("for (int i = start + 1, end = b.length(); i < end; i++)");
    writer.beginControlFlow("if (b.charAt(i) == '{')");
    writer.emitStatement("return i");
    writer.endControlFlow();
    writer.endControlFlow();
    writer.emitStatement("return -1");
    writer.endMethod();
    writer.emitEmptyLine();

    writer.beginMethod("String", "tokenAt", EnumSet.of(PRIVATE, STATIC), "SpannableStringBuilder",
        "b", "int", "start");
    writer.beginControlFlow("for (int i = start + 1, end = b.length(); i < end; i++)");
    writer.emitStatement("char current = b.charAt(i)");
    writer.beginControlFlow("if (current == '}')");
    writer.emitStatement("return b.subSequence(start + 1, i).toString()");
    writer.endControlFlow();
    writer.beginControlFlow("if (current < 'a' || current > 'z')");
    writer.emitStatement("break");
    writer.endControlFlow();
    writer.endControlFlow();
    writer.emitStatement("return null");
    writer.endMethod();

    writer.endType();
  }

  private static String classNameOf(Phrase phrase) {
    return "Phrase_" + phrase.name;
  }
}
