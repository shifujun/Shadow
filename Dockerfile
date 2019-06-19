FROM ubuntu:18.04

LABEL maintainer="shifujun@foxmail.com" \
      reference="https://github.com/mindrunner/docker-android-sdk" \
      flavour="shadow-build-cache" \
      sdk-tools-version="sdk-tools-linux-4333796"

ENV ANDROID_SDK_HOME /opt/android-sdk-linux
ENV ANDROID_SDK_ROOT /opt/android-sdk-linux
ENV ANDROID_HOME /opt/android-sdk-linux
ENV ANDROID_SDK /opt/android-sdk-linux

ENV DEBIAN_FRONTEND noninteractive

RUN mkdir -p /etc/ssl/certs/
COPY files/ca-certificates.crt /etc/ssl/certs/

# Install required tools
# Dependencies to execute Android builds

RUN dpkg --add-architecture i386 && apt-get update -y && apt-get install -y \
  curl \
  expect \
  git \
  make \
  libc6:i386 \
  libgcc1:i386 \
  libncurses5:i386 \
  libstdc++6:i386 \
  zlib1g:i386 \
  openjdk-8-jdk \
  wget \
  unzip \
  vim \
  openssh-client \
  locales \
  iproute2 \
  && apt-get clean

RUN  rm -rf /var/lib/apt/lists/* && localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8

ENV LANG en_US.UTF-8

RUN groupadd android && useradd -d /opt/android-sdk-linux -g android -u 1000 android

COPY tools /opt/tools

COPY licenses /opt/licenses

WORKDIR /opt/android-sdk-linux

RUN /opt/tools/install-sdk-with-user-android.sh

# clone Shadow的源码
RUN git clone https://github.com/Tencent/Shadow.git /opt/shadow

# 构建Shadow以自动安装所需的SDK，并缓存依赖
RUN cd /opt/shadow \
  && ./gradlew build \
  && ./gradlew -p projects/sdk/core :transform:test \
  && ./gradlew -p projects/sdk/core :gradle-plugin:test \
  && cd / && rm -rf /opt/shadow