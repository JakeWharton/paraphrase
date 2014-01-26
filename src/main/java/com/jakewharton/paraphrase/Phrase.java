package com.jakewharton.paraphrase;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

final class Phrase {
  static Phrase from(String name, String string) {
    return new Phrase(name, string, tokensFrom(string));
  }

  static List<Token> tokensFrom(String string) {
    List<Token> tokens = new ArrayList<>();
    int index = 0;
    char current;
    while (index < string.length()) {

    }
    return tokens;
  }

  final String name;
  final String string;
  final List<Token> tokens;

  Phrase(String name, String string, List<Token> tokens) {
    this.name = name;
    this.string = string;
    this.tokens = ImmutableList.copyOf(tokens);
  }

  static final class Token {
    final String name;
    final int start;
    final int end;

    Token(String name, int start, int end) {
      this.name = name;
      this.start = start;
      this.end = end;
    }
  }
}
