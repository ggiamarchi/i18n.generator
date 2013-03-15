[![Build Status](https://travis-ci.org/ggiamarchi/i18n.generator.png?branch=master)](https://travis-ci.org/ggiamarchi/i18n.generator)

<hr/>

# I18N Generator Maven Plugin #

 * <a href="#abstract">Abstract</a>
 * <a href="#quickstart">Quickstart</a>
 * <a href="#user-guide">User guide</a>
 
<hr/>

### Abstract ###

This project provides a Maven plugin that generate Java class with methods that match i18n properties. Calling methods
on the generated interface rather that reference string property keys in source code prevent never reference a
non-existing key.

For instance, assume that we have two java property files. The fist one, `messages.properties`, provides english messages
(the default language in this case) for an application

```
hello.world=Hello everybody
hello.to.somebody=Hello {0} {1}
```

And the second one, `messages_fr_FR.properties`, provides the same messages in french

```
hello.world=Bonjour tout le monde
hello.to.somebody=Bonjour {0} {1}
```

The purpose of the plugin is to generate a java interface (and its implementation, as we'll see later) with methods
that match properties defined in `*.properties` files. The generated interface looks something like this one

```java
public interface Messages {

  String hello_world();

  String hello_to_somebody(String arg0, String arg1);

}
```

An implementation class for this interface is generated as well. The goal of this implementation is to return the value
corresponding to the right language for each interface's method. "The right language" means the one according to either
the defaut locale or the one given by  a LocaleProvider, an interface that can be implemented to compute the locale on
the fly at runtime. For example, you would like to get the locale from the user session in case of a web application.

```java
public class MessagesImpl implements Messages {

  @Override
  public String hello_world() {
    // ...
  }
  
  @Override
  public String hello_to_somebody(String arg0, String arg1) {
    // ...
  }
  
  // Some technical stuffs...
  
}
```

Then, you finally just have to use these generated class in you application like any another. For example,

```java
Messages messages = ... // Instanciate the MessagesImpl class the way you want. With the new keywork, a Spring lookup, ...
String helloMe = messages.hello_to_somebody("Guillaume", "Giamarchi");
```

That's all folks !

<hr/>

### Quickstart ###

_(Available soon)_

<hr/>

### User guide ###

_(Available soon)_

<hr/>