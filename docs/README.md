```shell
https://services.gradle.org/distributions/gradle-7.2-bin.zip

unzip gradle-7.2-bin.zip
~/git/gradle-7.2/bin/gradle build -x test

cd maxkey-web-frontend
sudo docker run -ti --privileged --volume="$(pwd)":/maxkey-web-frontend -v "$(pwd)"/root:/root --rm node:14 bash
cd /maxkey-web-frontend
npm install -g cnpm --registry=https://registry.npm.taobao.org
cd maxkey-web-app
cnpm install
# npm install
npm run build
cd ../maxkey-web-mgt-app
cnpm install
# npm install
npm run build

cd docker
cp -r ../build/MaxKey-v3.3.3GA docker-maxkey
cp -r ../build/MaxKey-v3.3.3GA docker-maxkey-mgt

cp -r ../build/MaxKey-v3.5.0GA docker-maxkey
cp -r ../build/MaxKey-v3.5.0GA docker-maxkey-mgt

sudo docker-compose up --build -d

sudo docker-compose build
sudo docker-compose build maxkey
sudo docker-compose build maxkey-mgt

sudo docker-compose up
sudo docker-compose up -d

sudo docker-compose up -d maxkey
sudo docker-compose up -d maxkey-mgt

sudo docker-compose down

sudo docker-compose logs -f

sudo docker run -it --rm openjdk:8-jre bash

http://49.232.6.131:8091/maxkey-mgt/login
https://sso-test.7otech.com/maxkey-mgt/login
admin
maxkey

test
Sso@2022

https://49.232.6.131:8092/maxkey/login
https://sso-test.7otech.com/maxkey/login
admin
maxkey


mysql -h172.21.16.11 -uroot -p -P3316
maxkey
use maxkey

UPDATE mxk_apps
JOIN (select icon from mxk_apps where name='Jira') a
SET mxk_apps.icon = a.icon
WHERE name='test';

select icon from mxk_apps where name='test';

# cas服务端地址
server-url-prefix: https://sso-test.7otech.com/maxkey/authz/cas/
# cas服务端登陆地址
server-login-url: https://sso-test.7otech.com/maxkey/authz/cas/login

https://sso-test.7otech.com/maxkey/authz/cas/login?service=https%3A%2F%2Fsso-test.7otech.com%2Fmaxkey%2Fauthz%2Fcas%2F%2Fcore%2Fauth%2Fcas%2Flogin%2F%3Fnext%3D%252F

https://sso-test.7otech.com/maxkey/metadata/saml20/mxk_metadata_714217403415789568.xml

http://49.232.6.131:8074/login
monitor
maxkey

127.0.0.1  sso.maxkey.top
127.0.0.1  tokenbased.demo.maxkey.top
127.0.0.1  cas.demo.maxkey.top
127.0.0.1  oauth.demo.maxkey.top
```

```shell
sudo yum install openldap

sudo docker run -p 389:389 -p 636:636 --name my-openldap-container --detach osixia/openldap:1.5.0
sudo docker run -p 389:389 -p 636:636 \
	--env LDAP_ORGANISATION="7otech" \
	--env LDAP_DOMAIN="7otech.com" \
	--env LDAP_ADMIN_PASSWORD="admin" \
	--detach osixia/openldap:1.5.0
sudo docker exec my-openldap-container ldapsearch -x -H ldap://localhost -b dc=7otech.com,dc=org -D "cn=admin,dc=7otech.com,dc=org" -w admin

sudo docker run -p 8074:443 \
        --env PHPLDAPADMIN_LDAP_HOSTS=172.21.16.11 \
        --detach osixia/phpldapadmin:0.9.0
        
cn=admin,dc=7otech,dc=com
admin

ldap://172.21.16.11:389
dc=7otech,dc=com
```

```shell
iam
ldap
openldap
ldap server
phpldapadmin
ldap admin
freeipa

https://www.cnblogs.com/franzlistan/p/12228623.html
Appacheds
https://directory.apache.org/apacheds/
https://github.com/goodrain/rainbond
https://dromara.org/zh/
```

```
~/.gradle/wrapper/dists/gradle-7.2-bin/2dnblmf4td7x66yl1d74lt32g/gradle-7.2/bin/gradle --help

Welcome to Gradle 7.2!

Here are the highlights of this release:
 - Toolchain support for Scala
 - More cache hits when Java source files have platform-specific line endings
 - More resilient remote HTTP build cache behavior

For more details see https://docs.gradle.org/7.2/release-notes.html


USAGE: gradle [option...] [task...]

-?, -h, --help                     Shows this help message.
-a, --no-rebuild                   Do not rebuild project dependencies.
-b, --build-file                   Specify the build file. (deprecated)
--build-cache                      Enables the Gradle build cache. Gradle will try to reuse outputs from previous builds.
-c, --settings-file                Specify the settings file. (deprecated)
--configuration-cache              Enables the configuration cache. Gradle will try to reuse the build configuration from previous builds. [incubating]
--configuration-cache-problems     Configures how the configuration cache handles problems (fail or warn). Defaults to fail. [incubating]
--configure-on-demand              Configure necessary projects only. Gradle will attempt to reduce configuration time for large multi-project builds. [incubating]
--console                          Specifies which type of console output to generate. Values are 'plain', 'auto' (default), 'rich' or 'verbose'.
--continue                         Continue task execution after a task failure.
-D, --system-prop                  Set system property of the JVM (e.g. -Dmyprop=myvalue).
-d, --debug                        Log in debug mode (includes normal stacktrace).
--daemon                           Uses the Gradle Daemon to run the build. Starts the Daemon if not running.
--export-keys                      Exports the public keys used for dependency verification.
-F, --dependency-verification      Configures the dependency verification mode (strict, lenient or off)
--foreground                       Starts the Gradle Daemon in the foreground.
-g, --gradle-user-home             Specifies the gradle user home directory.
-I, --init-script                  Specify an initialization script.
-i, --info                         Set log level to info.
--include-build                    Include the specified build in the composite.
-M, --write-verification-metadata  Generates checksums for dependencies used in the project (comma-separated list)
-m, --dry-run                      Run the builds with all task actions disabled.
--max-workers                      Configure the number of concurrent workers Gradle is allowed to use.
--no-build-cache                   Disables the Gradle build cache.
--no-configuration-cache           Disables the configuration cache. [incubating]
--no-configure-on-demand           Disables the use of configuration on demand. [incubating]
--no-daemon                        Do not use the Gradle daemon to run the build. Useful occasionally if you have configured Gradle to always run with the daemon by default.
--no-parallel                      Disables parallel execution to build projects.
--no-scan                          Disables the creation of a build scan. For more information about build scans, please visit https://gradle.com/build-scans.
--no-watch-fs                      Disables watching the file system.
--offline                          Execute the build without accessing network resources.
-P, --project-prop                 Set project property for the build script (e.g. -Pmyprop=myvalue).
-p, --project-dir                  Specifies the start directory for Gradle. Defaults to current directory.
--parallel                         Build projects in parallel. Gradle will attempt to determine the optimal number of executor threads to use.
--priority                         Specifies the scheduling priority for the Gradle daemon and all processes launched by it. Values are 'normal' (default) or 'low'
--profile                          Profile build execution time and generates a report in the <build_dir>/reports/profile directory.
--project-cache-dir                Specify the project-specific cache directory. Defaults to .gradle in the root project directory.
-q, --quiet                        Log errors only.
--refresh-dependencies             Refresh the state of dependencies.
--refresh-keys                     Refresh the public keys used for dependency verification.
--rerun-tasks                      Ignore previously cached task results.
-S, --full-stacktrace              Print out the full (very verbose) stacktrace for all exceptions.
-s, --stacktrace                   Print out the stacktrace for all exceptions.
--scan                             Creates a build scan. Gradle will emit a warning if the build scan plugin has not been applied. (https://gradle.com/build-scans)
--status                           Shows status of running and recently stopped Gradle Daemon(s).
--stop                             Stops the Gradle Daemon if it is running.
-t, --continuous                   Enables continuous build. Gradle does not exit and will re-execute tasks when task file inputs change.
--update-locks                     Perform a partial update of the dependency lock, letting passed in module notations change version. [incubating]
-v, --version                      Print version info.
-w, --warn                         Set log level to warn.
--warning-mode                     Specifies which mode of warnings to generate. Values are 'all', 'fail', 'summary'(default) or 'none'
--watch-fs                         Enables watching the file system for changes, allowing data about the file system to be re-used for the next build.
--write-locks                      Persists dependency resolution for locked configurations, ignoring existing locking information if it exists
-x, --exclude-task                 Specify a task to be excluded from execution.
```