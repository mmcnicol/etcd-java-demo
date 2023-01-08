FROM quay.io/wildfly/wildfly:27.0.0.Final-jdk11

ADD ./target/etcd-java-demo-1.0-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/

#RUN ./wildfly/bin/add-user.sh -u 'mgr' -p 'jboss123#'

RUN /opt/jboss/wildfly/bin/add-user.sh mgr jboss123# --silent

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
 