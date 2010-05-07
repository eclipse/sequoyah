export JAVA_HOME=/opt/public/dsdp/JDKs/ibm-java2-ppc-50
export PATH=$JAVA_HOME/bin:$PATH
export BUILD_BASE_PATH=/opt/public/dsdp/sequoyah/helios
cd /opt/public/dsdp/sequoyah/helios
rm -rf src
cd build/sequoyah
ant -buildfile build.xml -Dpublish=true

