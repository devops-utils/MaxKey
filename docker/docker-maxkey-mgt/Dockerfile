FROM openjdk:8-jre

WORKDIR /home/sso
COPY MaxKey-v3.3.3GA .
COPY bin .

RUN ls

ENV TZ=Asia/Shanghai \
    DATABASE_HOST=maxkey-mysql \
    DATABASE_PORT=3306 \
    DATABASE_NAME=maxkey \
    DATABASE_USER=root \
    DATABASE_PWD=maxkey

ENTRYPOINT ["start_maxkey_mgt.sh"]