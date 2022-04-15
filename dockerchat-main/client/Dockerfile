FROM openjdk:11

COPY MtClient.java /deployments/
COPY ClientListener.java /deployments/
RUN cd /deployments && javac *.java
CMD cd /deployments; java MtClient

