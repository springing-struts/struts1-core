Springing struts1 core module
=============================

An experimental project aimed at migrating legacy Struts 1.x-based applications
to a Spring Boot architecture with minimal code changes. Springing Struts1 is a
Spring Boot-based web framework that provides a Struts 1.x-compatible API,
enabling legacy Java systems to migrate to a modern architecture with reduced
cost and risk.

How to use
------------

Springing Struts 1 Core Module works as a drop-in replacement for the following
modules:

- org.apache.struts:struts-core:1.x
- org.apache.struts:struts-extras:1.x
- org.apache.struts:struts-taglib:1.x
- org.apache.struts:struts-tiles:1.x
- commons-validator:commons-validator:1.x
- javax.servlet:servlet-api:2.x

For example, if your project is built with Maven, replacing the `<dependency>`
entries for the above modules with the following entry will work.

```xml
    <dependency>
      <groupId>io.github.iwauo.springing-struts</groupId>
      <artifactId>struts1-core</artifactId>
      <version>0.0.5</version>
    </dependency>
```

Examples
------------

The following projects demonstrate migration from the official Struts 1.x demo
applications using this library:

- **[Struts 1 Examples](https://github.com/springing-struts/example-struts1-examples)**
  - [Live Demo](http://129.146.59.150)

- **[Struts 1 Mail Reader](https://github.com/springing-struts/example-struts1-mailreader)**


How to build
------------

### Prerequisites
- A Unix-based system (Mac, WSL2, SteamOS, etc.)
- Mise version manager
 
### Initial setup

Run the following commands in your terminal.
```bash
git clone https://github.com/springing-struts/struts1-core.git
```

```bash
cd struts1-core
```

```bash
mise install
```

### build

```bash
./struts1-core/scripts/build.sh
```