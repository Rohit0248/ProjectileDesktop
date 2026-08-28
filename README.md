# Projectile Lab Desktop

A standalone desktop teaching app for secondary-school projectile motion.

## Run

Requires Java 17+ if running the JAR directly:

    java -jar ProjectileLab.jar

The `dist/` folder can contain a packaged Linux application image when built on Linux with `jpackage`.

## Windows

This JAR is cross-platform, but a native Windows `.exe` must be packaged on a Windows runner. A GitHub Actions workflow can produce that `.exe` using `jpackage`.
