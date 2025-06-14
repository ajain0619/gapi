FROM docker.ouroath.com:4443/ssp/geneva-builder:1.1111.0

ENV TZ US/Eastern
RUN sh -c "ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone"

RUN mkdir /workspace /workspace-sd
ADD ./tests-acceptance /workspace-sd/tests-acceptance
ADD ./golden-data /workspace-sd/golden-data
ADD ./pom.xml /workspace-sd/pom.xml
ADD ./.mvn /workspace-sd/.mvn

RUN apt-get -y update && apt-get -y install python3-pip python3-dev build-essential jq procps && pip install awscli

RUN cd /bin && curl -O https://artifactory.ouroath.com/artifactory/core-tech/releases/aws-builder-creds/Linux/aws-builder-creds && chmod 755 aws-builder-creds && \
    curl -O https://artifactory.ouroath.com/artifactory/core-tech/releases/zms-cli/2.0.8/Linux/zms-cli && chmod 755 zms-cli && \
    curl -O https://artifactory.ouroath.com/artifactory/core-tech/releases/ckms-remotecli/1.1.21/Linux/ckms-remotecli && chmod 755 ckms-remotecli

ADD tests-acceptance/scripts/docker/run-tests.sh /workspace-sd/tests-acceptance
RUN chmod 755 /workspace-sd/tests-acceptance/run-tests.sh
