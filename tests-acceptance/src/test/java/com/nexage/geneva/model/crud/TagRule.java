package com.nexage.geneva.model.crud;

public class TagRule {

  public String pid;
  public String tag_pid;
  public String target;
  public String target_type;
  public String rule_type;
  public String param_name;
  public String version;

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getTag_pid() {
    return tag_pid;
  }

  public void setTag_pid(String tag_pid) {
    this.tag_pid = tag_pid;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getTarget_type() {
    return target_type;
  }

  public void setTarget_type(String target_type) {
    this.target_type = target_type;
  }

  public String getRule_type() {
    return rule_type;
  }

  public void setRule_type(String rule_type) {
    this.rule_type = rule_type;
  }

  public String getParam_name() {
    return param_name;
  }

  public void setParam_name(String param_name) {
    this.param_name = param_name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(compareClones(o))) return false;
    TagRule that = (TagRule) o;

    if (pid != null ? !pid.equals(that.pid) : that.pid != null) return false;
    if (tag_pid != null ? !tag_pid.equals(that.tag_pid) : that.tag_pid != null) return false;
    return !(version != null ? !version.equals(that.version) : that.version != null);
  }

  public boolean compareClones(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TagRule that = (TagRule) o;

    if (target != null ? !target.equals(that.target) : that.target != null) return false;
    if (target_type != null ? !target_type.equals(that.target_type) : that.target_type != null)
      return false;
    if (rule_type != null ? !rule_type.equals(that.rule_type) : that.rule_type != null)
      return false;
    return !(param_name != null ? !param_name.equals(that.param_name) : that.param_name != null);
  }

  @Override
  public int hashCode() {
    int result = pid != null ? pid.hashCode() : 0;
    result = 31 * result + (tag_pid != null ? tag_pid.hashCode() : 0);
    result = 31 * result + (target != null ? target.hashCode() : 0);
    result = 31 * result + (target_type != null ? target_type.hashCode() : 0);
    result = 31 * result + (rule_type != null ? rule_type.hashCode() : 0);
    result = 31 * result + (param_name != null ? param_name.hashCode() : 0);
    result = 31 * result + (version != null ? version.hashCode() : 0);
    return result;
  }
}
