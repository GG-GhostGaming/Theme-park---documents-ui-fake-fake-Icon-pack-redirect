#!/usr/bin/env bash
set -e

# Gradle wrapper script generated for Gradle 8.5
# Minimal stub to call the installed gradle if wrapper jar is missing.
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)

if [ -x "$SCRIPT_DIR/gradlew" ] && [ "$0" != "$SCRIPT_DIR/gradlew" ]; then
  exec "$SCRIPT_DIR/gradlew" "$@"
fi

# If gradle wrapper jar exists, run it
if [ -f "$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar" ]; then
  java -jar "$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
else
  # Fallback to system gradle if available
  if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
  else
    echo "No gradle wrapper or system gradle found."
    exit 1
  fi
fi
