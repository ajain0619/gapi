# Messaging

## Overview

Geneva's Messaging SDK provides messaging APIs to Geneva's developers.
It uses AWS messaging services technologies:
 * [Amazon SNS]
 * [Amazon SQS] 
 * [Spring Cloud AWS messaging](https://spring.io/projects/spring-cloud-aws#overview) 

### Amazon SNS

This Amazon's service provides pub/sub real time functionality

The basic units of Amazon SNS are topics and subscriptions. A producer publishes a message
to a topic by its name. Then the topic forwards the message to all of its subscribers.
Subscribers can be: SQS queue, lambda, HTTP endpoint or SMS. Messages are not persisted on the server
so subscribers will lose messages if they are not connected.  

More on [Amazon SNS]

### Amazon SQS

This Amazon's service provides persistent queue functionality

The basic unit of Amazon SQS is a queue. Messages are sent to the queue and then consumed by consumers. 
Messages are persisted on the server so messages are not lost when consumers are not connected.

More on [Amazon SQS]

## Architecture

The basic architecture of the messaging infrastructure is based on queues subscribed to topics, which are categorized 
by entity. For example, a topic for placement's messages should forward all messages to queues wishing to consume placement's
messages. The Amazon SNS lets you use a filter policy to forward messages to subscribers per header's values. 

## Setup

### Create topics/queues/subscriptions

Setting up topics, queues and subscriptions is done in the following [repository] using CloudFormation

### Naming conventions

For topics, `geneva-messaging-<environment>-<entity>`. For example `geneva-messaging-uat-company` is the name of a topic 
for company related messages in the UAT environment.  
For queues, `geneva-messaging-<environment>-<entity>-<consumer>`. For example `geneva-messaging-prod-placement-buyer` is 
the name of a queue for placement related messages in the production environment used by the `buyer` consumer.  
Pay attention that the prefixes of both topics and queues are set automatically by the infrastructure.  

## Message

Messages in Amazon SNS and Amazon SQS are string based, which allows the use of JSON based messages.
A message consists of headers and a body. The following headers are used as part of the infrastructure:

| Name | Description | Default |
| ---  | --- | --- |
| contentType | used for message conversion | application/json |
| replyChannel | channel name used for reply | |
| errorChannel | channel name used for error | |
| h-correlationId | used with command message waiting for reply | |
| h-replyCorrelationId | used with reply message | |
| h-subOperation | message type used for filtering purposes | |
| h-source | source of message | |
| h-messagingTraceId | message trace ID | Automatically generated |

custom headers can be added if required

### Message types

There are two types of message communications: A command message is an outgoing message for which the producer waits for 
an incoming reply message, and an event message which is an outgoing message used to update consumers about a change made 
in the application's state.

Outgoing command message should have the `h-correlationId` header set. The reply message should reply with 
the same correlation ID using the `h-replyCorrelationId` header. These headers are used by the `messaging-recovery-dispatcher`
for error detection. This service uses a retry mechanism to recover from a scenario in which a command message
wasn't replied. [See more](https://git.ouryahoo.com/SSP/messaging-recovery-dispatcher)

## Message operations

### Publish message

To publish a message to a topic, use the `MessagePublisher` component
```java
final MessageHeaders messageHeaders =
        MessageHeaders.builder().withSubOperation("message_event").build();
final QueueListenerStub.MessageSample.MessageSampleBuilder message =
        QueueListenerStub.MessageSample.builder().message("hu");
messagePublisher.publish(Topic.PLACEMENT, message, messageHeaders);
```

### Consume message

To consume a message from a queue, use `@QueueListener` with the queue's name as the value and optionally if you want
to filter the incoming message by the message's headers then add a filter attribute with the headers key-value pairs upon
which you want to filter. Pay attention that the filter key-value pairs are case sensitive. The `@Payload` annotation is used
to label the incoming message body. if it's a JSON message then the message will be converted to Java object automatically.
```java
@QueueListener(value = "filtered_queue", filter = "h-subOperation=create")
public void onCreateMessage(@Payload CreateMessage message, @Headers Map<String, Object> headers) {
}
```

## Local environment

To simulate AWS on local environment, `localstack` is used. [See more] 
(`localstack` is started automatically when the server starts up)  
You can use AWS CLI with `localstack` to define topics, queues and subscriptions to check messaging flows locally

Some useful commands:
```shell script
aws --endpoint=http://localhost:4566 sns create-topic --name geneva-messaging-dev-placement
aws --endpoint=http://localhost:4566 sns publish --topic-arn <arn> --message "hello topic"
aws --endpoint=http://localhost:4566 sqs create-queue --queue-name geneva-messaging-dev-placement-buyer
aws --endpoint=http://localhost:4566 sqs send-message --queue-url <url> --message-body "hello queue"
```

## Troubleshoot tools

### Monitoring

The following Yamas dashboard(s) show the status of the messaging infrastructure and the messaging SDK's metrics. Link TBD

### Logs

Producers and consumers can leverage the `h-messagingTraceId` header to create cross service logging thread using the MDC
functionality. See example from the `buyer-mapping-async` service.

![MDC example](images/MDC%20example.png)

Service providers can use the [messaging-common](https://git.ouryahoo.com/SSP/messaging-common) library for MDC 
implementation

[Amazon SNS]: https://docs.aws.amazon.com/sns/latest/dg/welcome.html


[Amazon SQS]: https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html


[repository]: https://git.ouryahoo.com/SSP/cloudformation-geneva-platform


[See more]: https://github.com/localstack/localstack
