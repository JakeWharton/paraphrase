Paraphrase
==========

An experimental Gradle plugin which generates compile-safe format string builders.

**Don't** write your format strings like this: :thumbsdown:
```xml
<string name="greeting">Hello, %1$s! My name is %2$s.</string>
```

**Don't** suffer through positional argument formatting like this: :thumbsdown:
```java
CharSequence greeting = res.getText(R.string.greeting, "GitHub user", "Jake Wharton");
```

When you inevitably change the format string you will get a crash at runtime. Or even worse, a
silent malformed replacement!

**Do** write your format strings like this: :thumbsup:
```xml
<string name="greeting">Hello, {other_name}! My name is {my_name}.</string>
```

**Do** enjoy formatting them like this: :thumbsup:
```java
CharSequence greeting = Phrase.greeting()
    .other_name("GitHub user")
    .my_name("Jake Wharton")
    .build(this);
```

Now what happens when you change the format?
```xml
<string name="greeting">Hello! My name is {my_name}.</string>
```
```
Example.java:34: error: cannot find symbol
      .other_name("GitHub user")
      ^
  symbol:   method other_name(String)
  location: class Phrase_greeting
```
Build failures instead of runtime crashes! :thumbsup: :thumbsup: :thumbsup:



**This library is experimental, under-tested, and could really use your help.**

Give it a whirl and send some pull requests! Inspired by [Phrase][1] and a
[genius comment by Mark Carter][2] on its announcement.



Usage
-----

*Note:* Currently only deployed as `1.0.0-SNAPSHOT` for experimentation. Subject to API changes.
A release will come soon once I verify the code isn't absolutely terrible.

Apply the plugin in your `build.gradle`:
```groovy
buildscript {
  repositories {
    mavenCentral()
    maven url: 'http://oss.sonatype.org/content/repositories/snapshots/'
  }
  dependencies {
    classpath 'com.android.tools.build:gradle:0.8.+'
    classpath 'com.jakewharton.paraphrase:paraphrase:1.0.0-SNAPSHOT'
  }
}

apply plugin: 'android'
apply plugin: 'paraphrase'
```



To Do
-----

 * Compile-time validation of format string problems.
 * Verify spannable strings actually work...
 * Test fixtures that fail.
 * Tests for generated `AbstractPhrase`.
 * Emit string resource XML comment as method Javadoc.



License
--------

    Copyright 2014 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



 [1]: http://github.com/square/phrase
 [2]: http://corner.squareup.com/2014/01/phrase.html#comment-1214642643
