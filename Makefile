VERSION := 0.0.0-SNAPSHOT

MAVEN_SETTINGS = .mvn/settings.xml

SKIP_TEST ?= false
.PHONY: start
start:
	./pre-integration.sh
	./mvnw --settings $(MAVEN_SETTINGS) clean spotless:check package -Drevision=$(VERSION) -Dmaven.test.skip=${SKIP_TEST}
	java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044 \
		-Dcom.sun.management.jmxremote.port=7777 \
		-Dcom.sun.management.jmxremote.authenticate=false \
		-Dcom.sun.management.jmxremote.ssl=false \
		-Dspring.config.location=file:${PWD}/geneva-server/src/conf/local/application.properties \
		-Dgeneva.features=file:${PWD}/geneva-server/src/conf/local/FeatureVisibility.json \
		-Dlog4j.configurationFile=${PWD}/geneva-server/src/conf/local/log4j2.xml \
		-Dspring.profiles.active=aws,uncompress,redis-local,metrics,e2e-test,secure,messaging-local,debug \
		-Dport=8080 \
		-Duser.country=US -Duser.language=en -Duser.timezone=America/New_York \
		--add-opens java.base/java.lang=ALL-UNNAMED \
		-jar `ls ${PWD}/geneva-server/target/geneva-server-*.jar`

.PHONY: stop
stop:
	./post-integration.sh

STYLE_GOAL ?= check
.PHONY: style-check
style-check:
	./mvnw --settings $(MAVEN_SETTINGS) clean spotless:$(STYLE_GOAL)

.PHONY: style-apply
style-apply:
	@make style-check STYLE_GOAL=apply

.PHONY: start-quick
start-quick:
	@make start SKIP_TEST=true
