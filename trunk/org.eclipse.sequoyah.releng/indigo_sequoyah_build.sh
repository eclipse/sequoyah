export JAVA_HOME=/opt/public/common/jdk-1.5.0-22.x86_64
export PATH=$JAVA_HOME/bin:$PATH
export BUILD_BASE_PATH=/opt/public/tools/sequoyah/indigo
cd /opt/public/tools/sequoyah/indigo
rm -rf src
cd build/sequoyah
ant -buildfile build.xml

