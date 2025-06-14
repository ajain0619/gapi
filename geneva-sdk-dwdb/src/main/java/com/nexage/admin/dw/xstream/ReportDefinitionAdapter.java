package com.nexage.admin.dw.xstream;

import com.thoughtworks.xstream.XStream;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Component;

/** Converts report definition between xml and db model */
@Component
public class ReportDefinitionAdapter {

  public XmlReportDefinition getReportDefObject(String reportDefXml) {
    return (XmlReportDefinition) getXStream().fromXML(reportDefXml);
  }

  public XStream getXStream() {
    XStreamMarshaller marshaller = new XStreamMarshaller();
    marshaller.setAutodetectAnnotations(true);
    marshaller.setAnnotatedClasses(XmlReportDefinition.class);
    XStream xStream = marshaller.getXStream();
    // apply recommended security framework in XStream (see:
    // http://x-stream.github.io/security.html)
    xStream.allowTypes(
        new Class[] {
          XmlReportDefinition.class,
          XmlReportDefinition.DateRange.class,
          XmlReportDefinition.Interval.class
        });
    xStream.processAnnotations(XmlReportDefinition.class);
    return xStream;
  }
}
