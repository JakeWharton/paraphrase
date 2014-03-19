package com.jakewharton.paraphrase;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Phrase {
  private static final Pattern PHRASE = Pattern.compile("\\{([a-z_]+)\\}");

  static boolean isPhrase(String string) {
    return PHRASE.matcher(string).find();
  }

  static Phrase from(String name, String documentation, String string) {
    return new Phrase(name, documentation, string, tokensFrom(string));
  }

  static List<String> tokensFrom(String string) {
    List<String> tokens = new ArrayList<>();
    Matcher matcher = PHRASE.matcher(string);
    while (matcher.find()) {
      tokens.add(matcher.group(1));
    }
    Collections.sort(tokens); // Used to binary search at runtime.
    return ImmutableList.copyOf(tokens);
  }

  final String name;
  final String documentation;
  final String string;
  final List<String> tokens;

  Phrase(String name, String documentation, String string, List<String> tokens) {
    this.name = name;
    this.documentation = documentation;
    this.string = string;
    this.tokens = tokens;
  }

  @Override public String toString() {
    return "Phrase{name='"
        + name
        + "', documentation='"
        + documentation
        + "' string='"
        + string
        + "', tokens="
        + tokens
        + '}';
  }
}
