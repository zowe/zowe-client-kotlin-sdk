#!/bin/bash
#
# This program and the accompanying materials are made available under the terms of the
# Eclipse Public License v2.0 which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-v20.html
#
# SPDX-License-Identifier: EPL-2.0
#
# Copyright Contributors to the Zowe Project.
#

set -e

# WARNING: do not run the .sh file as is, use Gradle instead

echo "=========================================="
echo "Building Zowe Secrets Native Libraries"
echo "=========================================="

# Determine current platform
CURRENT_OS=$(uname -s | tr '[:upper:]' '[:lower:]')
CURRENT_ARCH=$(uname -m)

echo ""
echo "Current platform: $CURRENT_OS / $CURRENT_ARCH"

# Path to native libraries directory (2 levels up from src/secrets)
RESOURCES_DIR="../../main/resources/native"
# Path to built libs
TARGET_DIR="../target"

# Determine target for CURRENT platform
CURRENT_TARGET=""
BUILD_ALL=false

if [[ "$1" == "--all" ]]; then
  BUILD_ALL=true
  echo "Building for ALL platforms (requires cross-compilation tools)"
fi

# Determine target for current system
if [[ "$CURRENT_OS" == "linux"* ]]; then
  if [[ "$CURRENT_ARCH" == "aarch64" ]] || [[ "$CURRENT_ARCH" == "arm64" ]]; then
    CURRENT_TARGET="aarch64-unknown-linux-gnu"
  else
    CURRENT_TARGET="x86_64-unknown-linux-gnu"
  fi
elif [[ "$CURRENT_OS" == "darwin"* ]]; then
  if [[ "$CURRENT_ARCH" == "arm64" ]]; then
    CURRENT_TARGET="aarch64-apple-darwin"
  else
    CURRENT_TARGET="x86_64-apple-darwin"
  fi
else
  echo "Error: Unsupported OS: $CURRENT_OS"
  exit 1
fi

# List of targets to build
if [[ "$BUILD_ALL" == true ]]; then
  TARGETS=(
    "x86_64-unknown-linux-gnu"
    "aarch64-unknown-linux-gnu"
    "x86_64-apple-darwin"
    "aarch64-apple-darwin"
    "x86_64-pc-windows-gnu"
    "aarch64-pc-windows-gnu"
  )
else
  TARGETS=("$CURRENT_TARGET")
fi

echo ""
echo "Step 1: Installing Rust targets..."
for target in "${TARGETS[@]}"; do
  echo "  - Adding target: $target"
  rustup target add "$target" 2>/dev/null || echo "    (already installed)"
done

echo ""
echo "Step 2: Checking cross-compilation tools..."

# Set up cross-compilation linkers if needed
if [[ "$BUILD_ALL" == true ]]; then
  if [[ "$CURRENT_OS" == "linux"* ]]; then
    # Check for ARM64 cross-compiler
    if command -v aarch64-linux-gnu-gcc &> /dev/null; then
      export CARGO_TARGET_AARCH64_UNKNOWN_LINUX_GNU_LINKER=aarch64-linux-gnu-gcc
      echo "  Found ARM64 cross-compiler: aarch64-linux-gnu-gcc"
    else
      echo "  Warning: ARM64 cross-compiler not found"
      echo "  Install with: sudo apt-get install gcc-aarch64-linux-gnu"
      echo "  Skipping aarch64-unknown-linux-gnu"
      TARGETS=("${TARGETS[@]/aarch64-unknown-linux-gnu/}")
    fi

    # Check for MinGW cross-compiler for Windows
    if command -v x86_64-w64-mingw32-gcc &> /dev/null; then
      export CARGO_TARGET_X86_64_PC_WINDOWS_GNU_LINKER=x86_64-w64-mingw32-gcc
      echo "  Found MinGW cross-compiler: x86_64-w64-mingw32-gcc"
    else
      echo "  Warning: MinGW cross-compiler not found"
      echo "  Install with: sudo apt-get install mingw-w64"
      echo "  Skipping x86_64-pc-windows-gnu"
      TARGETS=("${TARGETS[@]/x86_64-pc-windows-gnu/}")
    fi

    # ARM64 Windows is not well supported on Linux
    echo "  Warning: ARM64 Windows cross-compilation not supported"
    echo "  Skipping aarch64-pc-windows-gnu"
    TARGETS=("${TARGETS[@]/aarch64-pc-windows-gnu/}")
  fi
fi

echo ""
echo "Step 3: Building native libraries..."

BUILD_SUCCESS=false

for target in "${TARGETS[@]}"; do
  # Skip empty elements
  [[ -z "$target" ]] && continue

  echo ""
  echo "Building for $target..."

  if cargo build --release --target "$target" 2>&1; then
    echo "  Build successful"
    BUILD_SUCCESS=true
  else
    echo "  Build failed for $target"
    if [[ "$BUILD_ALL" != true ]]; then
      echo "Error: Failed to build for current platform!"
      exit 1
    fi
  fi
done

if [[ "$BUILD_SUCCESS" != true ]]; then
  echo ""
  echo "Error: All builds failed!"
  exit 1
fi

echo ""
echo "Step 4: Creating resource directories..."
mkdir -p "$RESOURCES_DIR/linux/x86_64"
mkdir -p "$RESOURCES_DIR/linux/aarch64"
mkdir -p "$RESOURCES_DIR/macos/x86_64"
mkdir -p "$RESOURCES_DIR/macos/aarch64"
mkdir -p "$RESOURCES_DIR/windows/x86_64"
mkdir -p "$RESOURCES_DIR/windows/aarch64"
echo "  Directories created at: $RESOURCES_DIR"

echo ""
echo "Step 5: Copying libraries to resources..."

COPIED_COUNT=0

# Linux x86_64
if [ -f "$TARGET_DIR/x86_64-unknown-linux-gnu/release/libkeyring.so" ]; then
  cp "$TARGET_DIR/x86_64-unknown-linux-gnu/release/libkeyring.so" \
     "$RESOURCES_DIR/linux/x86_64/libkeyring.so"
  echo "  Copied: linux/x86_64/libkeyring.so"
  COPIED_COUNT=$((COPIED_COUNT + 1))
fi

# Linux ARM64
if [ -f "$TARGET_DIR/aarch64-unknown-linux-gnu/release/libkeyring.so" ]; then
  cp "$TARGET_DIR/aarch64-unknown-linux-gnu/release/libkeyring.so" \
     "$RESOURCES_DIR/linux/aarch64/libkeyring.so"
  echo "  Copied: linux/aarch64/libkeyring.so"
  COPIED_COUNT=$((COPIED_COUNT + 1))
fi

# macOS x86_64
if [ -f "$TARGET_DIR/x86_64-apple-darwin/release/libkeyring.dylib" ]; then
  cp "$TARGET_DIR/x86_64-apple-darwin/release/libkeyring.dylib" \
     "$RESOURCES_DIR/macos/x86_64/libkeyring.dylib"
  echo "  Copied: macos/x86_64/libkeyring.dylib"
  COPIED_COUNT=$((COPIED_COUNT + 1))
fi

# macOS ARM64
if [ -f "$TARGET_DIR/aarch64-apple-darwin/release/libkeyring.dylib" ]; then
  cp "$TARGET_DIR/aarch64-apple-darwin/release/libkeyring.dylib" \
     "$RESOURCES_DIR/macos/aarch64/libkeyring.dylib"
  echo "  Copied: macos/aarch64/libkeyring.dylib"
  COPIED_COUNT=$((COPIED_COUNT + 1))
fi

# Windows x86_64
if [ -f "$TARGET_DIR/x86_64-pc-windows-gnu/release/keyring.dll" ]; then
  cp "$TARGET_DIR/x86_64-pc-windows-gnu/release/keyring.dll" \
     "$RESOURCES_DIR/windows/x86_64/keyring.dll"
  echo "  Copied: windows/x86_64/keyring.dll"
  COPIED_COUNT=$((COPIED_COUNT + 1))
fi

# Windows ARM64
if [ -f "$TARGET_DIR/aarch64-pc-windows-gnu/release/keyring.dll" ]; then
  cp "$TARGET_DIR/aarch64-pc-windows-gnu/release/keyring.dll" \
     "$RESOURCES_DIR/windows/aarch64/keyring.dll"
  echo "  Copied: windows/aarch64/keyring.dll"
  COPIED_COUNT=$((COPIED_COUNT + 1))
fi

if [[ $COPIED_COUNT -eq 0 ]]; then
  echo "Error: No libraries were copied!"
  exit 1
fi

echo ""
echo "=========================================="
echo "Zowe Keyring Build Complete!"
echo "=========================================="
echo ""
echo "Location: $RESOURCES_DIR"
echo "Copied $COPIED_COUNT library(ies)"
echo ""
echo "Built libraries:"
find "$RESOURCES_DIR" -type f \( -name "*.so" -o -name "*.dylib" -o -name "*.dll" \) 2>/dev/null | sed 's/^/  /' || echo "  (none found)"
echo ""

if [[ "$BUILD_ALL" != true ]]; then
  echo "Note: Only built for current platform ($CURRENT_TARGET)"
  echo "To build for all platforms, run: ./build_secrets.sh --all"
  echo ""
fi