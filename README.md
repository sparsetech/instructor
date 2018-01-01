# Instructor
[![Join the chat at https://gitter.im/sparsetech/instructor](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sparsetech/instructor?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Instructor is a simple tool that allows you to generate single-page HTML manuals.

[![asciicast](https://asciinema.org/a/0urh4T5dDimpFF6gA7EbdjiSw.png)](https://asciinema.org/a/0urh4T5dDimpFF6gA7EbdjiSw)

## Features
* Load Markdown files
* Create single-page HTML documents
* Configurable using TOML file
* Generate table of contents
* Embed code listings
* Syntax highlighting using [highlight.js](https://highlightjs.org/)
* Support multiple themes (see [here](assets/templates))
* Set constants
* Logging with colour support

## Installation
You can create a self-contained JAR file using the following command:

```shell
sbt assembly
```

You can add the directory to the environment variable `$PATH`.

## Configuration
Create a TOML file in your project folder that looks as follows:

```toml
[meta]
title       = "Example User Manual"
author      = "John Doe"
affiliation = "My Company Ltd."
abstract    = "Short project description"

# Will be used to format dates and will be set in the HTML header
language = "en-GB"

# If set, a link for editing the file will be inserted after each chapter
#editSourceUrl = "https://github.com/username/project/edit/master/"

[input]
# Files can be listed here manually to change the order
paths = ["docs/*.md"]

#[output]
#highlightJsStyle = "tomorrow"

# Load version number from `version.sbt` and make it available via `%version%`
#[constants]
#inherit = "version.sbt"

# See http://github.com/sparsetech/leaf on how to generate this JSON file
#[listings]
#path = "manual/listings.json"
```

## Usage
Generate your manual using this command:

```shell
instructor manual.toml
```

You can specify an alternative output path using `-o`. Run `instructor` without parameters for more information.

## Examples
As an example, see the [Trail](http://sparse.tech/docs/trail.html) documentation which was generated from [this configuration](https://github.com/sparsetech/trail/blob/master/manual.toml).

## Licence
Instructor is licensed under the terms of the Apache v2.0 licence.

## Authors
* Tim Nieradzik
