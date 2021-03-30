#!/bin/bash

wget https://dl.google.com/android/repository/commandlinetools-linux-6858069_latest.zip -P/tmp && \
    unzip /tmp/commandlinetools-linux-6858069_latest.zip -d ~/android-sdk && \
    unlink /tmp/commandlinetools-linux-6858069_latest.zip && \
    export ANDROID_HOME=~/android-sdk && \
    mv $ANDROID_HOME/cmdline-tools/ $ANDROID_HOME/latest && \
    mkdir $ANDROID_HOME/cmdline-tools/ && \
    mv $ANDROID_HOME/latest $ANDROID_HOME/cmdline-tools/latest && \
    mkdir -p ~/.android && \
    touch ~/.android/repositories.cfg && \
    yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-26" && \
    yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
