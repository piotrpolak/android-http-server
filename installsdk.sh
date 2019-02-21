#!/bin/bash

wget https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip -P/tmp && \
    unzip /tmp/sdk-tools-linux-4333796.zip -d ~/android-sdk && \
    unlink /tmp/sdk-tools-linux-4333796.zip && \
    export ANDROID_HOME=~/android-sdk && \
    mkdir -p ~/.android && \
    touch ~/.android/repositories.cfg && \
    yes | $ANDROID_HOME/tools/bin/sdkmanager "platform-tools" "platforms;android-25"