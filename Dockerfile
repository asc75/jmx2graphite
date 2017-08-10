FROM openjdk:8u121-alpine

MAINTAINER Asaf Mesika <asaf.mesika@gmail.com>

RUN apk add --no-cache --update bash curl vim

# Add the package
ADD build/distributions/*.tar /

RUN mkdir -p /var/log/jmx2graphite

RUN mkdir /opt/jmx2graphite/ext_conf
VOLUME /opt/jmx2graphite/ext_conf

# Default Start
CMD /opt/jmx2graphite/bin/jmx2graphite
