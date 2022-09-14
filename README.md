# Minestom libraries for MMOs

A set of data-driven<sup>1</sup> libraries useful for creating MMO servers, based on Minestom.

blah blah more

<sup>1</sup> As in, they load static data for content, rather than hardcoding it. The Minecraft definition of
data-driven,
not [the general programming term](https://en.wikipedia.org/wiki/Data-driven_programming).

# Table of Contents

- [Background](#background)
- [Install](#install)
- [Project Structure](#project-structure)
    - [Modules](#modules)
- [Contributing](#contributing)
- [License](#license)

# Background

todo

# Install

todo

# Project Structure

The project is split into a set of modules, each one representing a unique feature. The modules may be used
independently of each other, although there are some required dependencies. See the individual module README
files for more information about usage and dependencies.

To achieve this separation, this project makes very heavy use of the [Java Service Provider Interface (SPI)]
(https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html). SPI is used to load implementation
classes from different modules when they are present, without loading them when they should not be. An example
is a better explanation: The loot module is a generic way of generating content randomly (collectively "loot").
When importing the loot module, there are no implementations of loot, so you cannot actually generate anything.
Other modules must provide loot by implementing the `LootEntry` interface. For example, the item module provides
an `ItemEntry` for generating items. `LootEntry` implementations are loaded using SPI, which means that when
the item module is present, the `ItemEntry` will be automagically loaded, but the item module may have a compile
only dependency on the loot module (e.g. it may be used without loot) because if loot is not present, `ItemEntry`
will never be loaded. This pattern is used in many places throughout the project.

The `common` module is a hard dependency of all other modules. It should be kept as minimal as possible. The `test`
module provides some testing utilities for the other modules. It is only a test dependency. Currently, it contains
a copy of the internal Minestom test framework, and some Minestom specific Truth subjects.

## Modules

- block-interactions: Provides block interactions, such as farming and ores.
- chat: Provides chat formatting and recording, along with (todo) cross server chat capabilities.
- common: See above.
- development: Development server, to be moved out of this project eventually.
- item: A system for defining custom items based on data files using components.
- loot-table: A generic system for generating content based on some data-driven rules.
- player: Player mechanic implementation and a combat system.
- test: See above.

# Contributing

Issues and PRs are welcome! Please refer to [CONTRIBUTING.md](CONTRIBUTING.md) for more information.

Local development is currently done using the `development` module (and of course tests). To run the development
server, execute `./gradlew :modules:development:run`.

# License

This project is licensed under the [MIT License](./LICENSE).
