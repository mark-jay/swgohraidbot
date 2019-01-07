FROM openjdk:8-jre-slim-stretch
MAINTAINER Magzhan Karassayev fallen.s4e@gmail.com

add . /

RUN apt-get update

# tesseract
RUN apt-get install -y tesseract-ocr tesseract-ocr-eng
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/tessdata

CMD [ "java", "-jar", "/opt/swgohraidbot-discrodbot/swgohraidbot-discrodbot-jar-with-dependencies.jar" ]
