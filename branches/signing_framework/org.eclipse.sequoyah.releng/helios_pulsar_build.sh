export JAVA_HOME=/opt/public/common/jdk-1.5.0-22.x86_64
export PATH=$JAVA_HOME/bin:$PATH
export BUILD_BASE_PATH=/opt/public/dsdp/sequoyah/helios
cd /opt/public/dsdp/sequoyah/helios
rm -rf src
cd build/pulsar
ant -buildfile build.xml

