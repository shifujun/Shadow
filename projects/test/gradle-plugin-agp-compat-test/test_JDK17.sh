#!/usr/bin/env bash

# 从Gradle 7.5和AGP 8.0开始支持和要求使用JDK 17，所以这个脚本中的待测版本需要在JDK 17环境下测试。

JAVA_MAJOR_VERSION=$(javap -verbose java.lang.Object | grep "major version" | cut -d " " -f5)
if [[ $JAVA_MAJOR_VERSION -ne 61 ]]; then
  echo "需要JDK 17!"
  exit 1
fi

source ./test_prepare.sh

# 测试版本来源
# https://developer.android.com/studio/releases/gradle-plugin
setGradleVersion 7.5
testUnderAGPVersion 7.4.1
