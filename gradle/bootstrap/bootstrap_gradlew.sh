#!/usr/bin/env sh

# Copyright (c) 2024 IBA Group.
#
# This program and the accompanying materials are made available under the terms of the
# Eclipse Public License v2.0 which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-v20.html
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#   IBA Group
#   Zowe Community

if [ ! -f gradle/wrapper/gradle-wrapper.jar ]; then
    echo "Gradle Wrapper not found. Attempting to download..."
    curl --silent --output gradle/wrapper/gradle-wrapper.jar \
        https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar
    rc=$?
    if [ $rc != 0 ]; then
        echo "Gradle wrapper download failed. Bootstrap failed."
        exit 1
    else
        echo "Gradle wrapper download success; bootstrap complete."
        exit 0
    fi
else
    echo "Gradle Wrapper found, bootstrap complete."
    exit 0
fi
