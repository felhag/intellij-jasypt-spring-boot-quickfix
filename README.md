# Jasypt Spring Boot Quickfix

![Build](https://github.com/felhag/intellij-jasypt-spring-boot-quickfix/workflows/Build/badge.svg)

<!-- Plugin description -->
Plugin to add support for Jasypt Spring Boot in Intellij. Quickly encrypt and decrypt  credentials in your application properties files. The passwords are safely stored using Intellij's [sensitive data API](https://plugins.jetbrains.com/docs/intellij/persisting-sensitive-data.html). Profiles are matched based on first letter (e.g. Jasypt password T should match `application-test.yml`, `application-t.yaml`, `application-tomething.yml` etc).
<!-- Plugin description end -->

![Jasypt Spring Boot Quickfix Intellij plugin](https://i.imgur.com/KrXfURS.gif)

## Installation
  Download the [latest release](https://github.com/felhag/intellij-jasypt-spring-boot-quickfix/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## How to use
1. Provide Jasypt passwords (<kbd>⚙️ Settings/Preferences</kbd> > <kbd>Other settings</kbd> > <kbd>Jasypt Spring Boot Quickfix</kbd>)
2. Open `application-profile.yml`
3. Put cursor on a value and apply quick fix (`ALT + ENTER`)

---

Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
