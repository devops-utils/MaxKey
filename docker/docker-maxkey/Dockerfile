FROM openjdk:8-jre

WORKDIR /home/sso
COPY MaxKey-v3.5.0GA .
COPY bin .

RUN chmod +x *.sh

ENV TZ=Asia/Shanghai \
    DATABASE_HOST=maxkey-mysql \
    DATABASE_PORT=3306 \
    DATABASE_NAME=maxkey \
    DATABASE_USER=root \
    DATABASE_PWD=maxkey

ENTRYPOINT ["/home/sso/start_maxkey.sh"]