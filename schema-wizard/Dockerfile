FROM jetty:9.3.8

RUN apt-get update

# TODO remove pcap native dependency?
# RUN apt-get install -y tcpdump software-properties-common

# Copy over files
ADD schwiz.war /var/lib/jetty/webapps
# ADD *.so /usr/lib/

# Update symbolic dynamic links
# RUN ["ldconfig"]

# Creating necessary dirs
RUN ["mkdir", "/uploads"]
RUN ["mkdir", "/logs"]
RUN ["touch", "/logs/sw.log"]

# Giving Jetty access permissions
RUN chown -R jetty:jetty /uploads /logs /var/lib/jetty