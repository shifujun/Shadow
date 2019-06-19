#!/bin/bash

mkdir -p /opt/android-sdk-linux/bin/
cp /opt/tools/android-env.sh /opt/android-sdk-linux/bin/
source /opt/android-sdk-linux/bin/android-env.sh

built_in_sdk=1

cd ${ANDROID_HOME}
echo "Set ANDROID_HOME to ${ANDROID_HOME}"

echo "Bootstrapping SDK-Tools"
#注意sdk-tools的版本号在Dockerfile的LABEL中有记录
wget http://dl.google.com/android/repository/sdk-tools-linux-4333796.zip \
  -O sdk-tools-linux.zip \
  && unzip sdk-tools-linux.zip \
  && rm sdk-tools-linux.zip

echo "Make sure repositories.cfg exists"
mkdir -p ~/.android/
touch ~/.android/repositories.cfg

echo "Copying Licences"
cp -rv /opt/licenses ${ANDROID_HOME}/licenses

echo "Copying Tools"
mkdir -p ${ANDROID_HOME}/bin
cp -v /opt/tools/*.sh ${ANDROID_HOME}/bin

echo "Accepting Licenses"
android-accept-licenses.sh "sdkmanager ${SDKMNGR_OPTS} --licenses"
