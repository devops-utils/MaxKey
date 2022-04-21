FROM openjdk:8-jre

WORKDIR /home/sso
COPY MaxKey-v3.3.3GA .
COPY bin .

ENV TZ=Asia/Shanghai \
    DATABASE_HOST=maxkey-mysql \
    DATABASE_PORT=3306 \
    DATABASE_NAME=maxkey \
    DATABASE_USER=root \
    DATABASE_PWD=maxkey

ENTRYPOINT ["/home/sso/start_maxkey.sh"]