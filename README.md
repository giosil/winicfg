# WINICfg

Utility classes to load and save INI configuration files.

The library provides the following class:

- `org.dew.ini.WINI` - INIConfig manager

## Examples

```java
Map<String, Object> config = WINI.loadResource("test.ini");
```

```java
Map<String, Object> config = WINI.load("file.ini");
```

```java
WINI.save("file.ini", config, comments);
```

## Build

- `git clone https://github.com/giosil/winicfg.git`
- `mvn clean install`

## Contributors

* [Giorgio Silvestris](https://github.com/giosil)
