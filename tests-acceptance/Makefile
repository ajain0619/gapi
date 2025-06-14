VERSION 		:= 0.0.0-SNAPSHOT
APP_NAME 		= geneva-api-tests-acceptance
STACK_NAME 		= ssp/$(APP_NAME)

-include cloud-config/make-defs/common.defs
-include cloud-config/make-defs/docker.defs
-include cloud-config/make-defs/release.defs

TEST 	:= GlobalTestRunner

ifeq ($(ENVIRONMENT),)
ENVIRONMENT := local
endif

ifeq ($(REGION),)
REGION := us-east-1
endif

MAVEN_SETTINGS = ../.mvn/settings.xml

.PHONY: build
build:
	cd ..; docker build  -f tests-acceptance/Dockerfile -t docker.ouroath.com:4443/$(STACK_NAME):$(VERSION) .

.PHONY: run
run:
	mvn clean spotless:check verify -Drevision=$(VERSION) -Denvironment=$(ENVIRONMENT) \
	-Dit.test=$(TEST) -Paws -Dspring.profiles.active=$(SPRING_PROFILE)

.PHONY: run-docker
run-docker:
	docker run \
		--name $(APP_NAME) \
		-v ${PWD}:/workspace/$(APP_NAME) \
		-v ${PWD}/golden-data:/workspace/golden-data \
		-v ${HOME}/.m2:/home/ec2-user/.m2 \
		-v ${HOME}/.m2/repository:/home/ec2-user/.m2/repository \
		docker.ouroath.com:4443/$(STACK_NAME):$(VERSION) $(VERSION) $(REGION)

.PHONY: local-setup
local-setup:
	@cd .. && rm -rf tmp/ || true && ./pre-test.sh

.PHONY: start
start: local-setup
	make run SPRING_PROFILE=global-runner,aws ENVIRONMENT=local

.PHONY: start-custom
start-custom: local-setup
	make run SPRING_PROFILE=custom-runner,aws ENVIRONMENT=local TEST=CustomTestRunner

STYLE_GOAL ?= check
.PHONY: style-check
style-check:
	./mvnw --settings $(MAVEN_SETTINGS) clean spotless:$(STYLE_GOAL)

.PHONY: style-apply
style-apply:
	@make style-check STYLE_GOAL=apply
