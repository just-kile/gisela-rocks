#context must contain certificate gisela-rocks-e88cde3d6ea4.p12

FROM ubuntu:14.10

MAINTAINER "Thomas Kipar"

RUN apt-get update
RUN apt-get install -y git
RUN apt-get -y install software-properties-common

RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections

RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN apt-get install -y oracle-java8-installer

ENV JAVA_HOME /usr/lib/jvm/java-8-oracle/

RUN git clone https://github.com/just-kile/gisela-rocks.git

WORKDIR gisela-rocks/gisela-rocks

RUN ./grailsw compile
ADD gisela-rocks-e88cde3d6ea4.p12 /gisela-rocks/gisela-rocks/certs/gisela-rocks-e88cde3d6ea4.p12
ENV GISELA_CERT /gisela-rocks/gisela-rocks/certs/gisela-rocks-e88cde3d6ea4.p12

EXPOSE 8080

ENTRYPOINT git pull origin master && ./grailsw run-app


