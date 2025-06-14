# Metrics

Metrics are enabled through spring profile `metrics`.

## Reporters

Currently the app creates two reporters:

* [JMX](https://docs.oracle.com/javase/tutorial/jmx/)
* [Datadog](https://www.datadoghq.com/)

### JMX

* `geneva.metrics.jmx.enabled`: Enable/Disable functionality. Default: `true`.
* `geneva.metrics.jmx.domain`: Define unique domain for JMX id. Default: `ssp.geneva.api`.

### Datadog

* `geneva.metrics.datadog.enabled`: Enable/Disable functionality. Default: `false`.
* `geneva.metrics.datadog.period`: Frequently for datadog agent push. Time unit in second. Default: `10`.

### Yamas

Each Geneva host is configured with a Datadog and Telegraf agent. The datadog agent is configured to
pull metrics from JMX and report them to the statsd agent. It is also configured to push the metrics
to `datadog.monitoring.yahoo.com` but this doesn't actually seem to do anything.

Telegraf agent is configured to pull the metrics from statsd and report them to Yamas

All the metrics can be found on:

- https://yamas.ouroath.com/
- https://stg-horizon.yamas.ouroath.com:4443

### Splunk

Geneva has a splunk dashboard that tracks the UI routes that are performing poorly and lists them from worst to best. 
We use a splunk forwarder to push the geneva-api logs to the splunk indexers. Our splunk forwarder configuration is set up by our ansible config.
If you would like more information visit the link [here](https://docs.splunk.com/Documentation/Forwarder/8.1.1/Forwarder/Configuretheuniversalforwarder).
Please see the dashboard with the yo link below.

- http://yo/ssp-splunk-dashboard

> Note: If you dont already have access please fill out the Jira template in order to create a ticket to gain access 
>to the dashboard http://yo/ssp-splunk-access
