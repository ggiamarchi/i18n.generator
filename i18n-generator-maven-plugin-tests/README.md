
# Implemented tests #

<hr/>

## i18n-generator-maven-plugin-test-001 ##

### Description ###

The basic test.

Single i18n bundle with two languages `messages.properties` and `messages_fr.properties`

### plugin configuration snippet ###

```xml
<configuration>
    <i18nMessagesBundle>
        <name>com.test.i18n.messages</name>
    </i18nMessagesBundle>
</configuration>
```


## i18n-generator-maven-plugin-test-002 ##

### Description ###

the same as i18n-generator-maven-plugin-test-001 but with bundle in the default package

### plugin configuration snippet ###

```xml
<configuration>
    <i18nMessagesBundle>
        <name>messages</name>
    </i18nMessagesBundle>
</configuration>
```


## i18n-generator-maven-plugin-test-003 ##

### Description ###

the same as i18n-generator-maven-plugin-test-002 but defined <interfaceName/> and <className/> in the
default package.

### plugin configuration snippet ###

```xml
<configuration>
    <i18nMessagesBundle>
        <name>messages</name>
        <interfaceName>Mess</interfaceName>
        <className>MessImpl</className>
    </i18nMessagesBundle>
</configuration>
```

## i18n-generator-maven-plugin-test-004 ##

### Description ###

the same as i18n-generator-maven-plugin-test-001 with a defined <outputDirectory/>

### plugin configuration snippet ###

```xml
<configuration>
    <i18nMessagesBundle>
        <name>com.test.i18n.messages</name>
    </i18nMessagesBundle>
    <outputDirectory>target/generated-sources/messages</outputDirectory>
</configuration>
```


## i18n-generator-maven-plugin-test-005 ##

### Description ###

the same as i18n-generator-maven-plugin-test-004 with the whole range of options defined

### plugin configuration snippet ###

```xml
<configuration>
    <i18nMessagesBundle>
        <name>com.test.i18n.messages</name>
        <interfaceName>messages.i18n.Mess</interfaceName>
        <className>messages.i18n.impl.MessImpl</className>
        <outputDirectory>target/generated-sources/mes</outputDirectory>
    </i18nMessagesBundle>
    <outputDirectory>target/generated-sources/messages</outputDirectory>
</configuration>
```


## i18n-generator-maven-plugin-test-006 ##

### Description ###

Two i18n bundle, each with at least two languages. One with the mimnimal configuration, and the other
with all default values overloaded. And a global <outputDirectory/> to make sure each bundle is generated
in its own directory.

### plugin configuration snippet ###

```xml
<configuration>
	<i18nMessagesBundles>
		<param>
			<name>com.test.i18n.hello</name>
			<interfaceName>messages.i18n.Hi</interfaceName>
			<className>messages.i18n.impl.HiImpl</className>
			<outputDirectory>target/generated-sources/messages/hi</outputDirectory>
		</param>
		<param>
			<name>com.test.i18n.bye</name>
		</param>
	</i18nMessagesBundles>
	<outputDirectory>target/generated-sources/messages/default</outputDirectory>
</configuration>
```
