Sequoyah Build Documentation

-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

Build process for Sequoyah:

All *_build.sh scripts run the build process.
All *_publish.sh scripts run the build process and then copy the build to the update site.

1) If you want to build the stable version of Sequoyah,
for Galileo (3.5.1), then use the scripts galileo_*.sh.
They will create a new build under 0.5_stable downloads folder.
[http://download.eclipse.org/sequoyah/downloads/drops/0.5_stable/]

2) If you want to build the latest version of Sequoyah,
for Helios (3.6.0), then use the scripts helios_sequoyah_*.sh.
They will create a new build under 0.5 downloads folder.
[http://download.eclipse.org/sequoyah/downloads/drops/0.5/]

3) If you want to build the latest version of Pulsar plugins,
for Helios (3.6.0), then use the scripts helios_pulsar_*.sh.
They will create a new build under 1.0.1 downloads folder.
[http://download.eclipse.org/sequoyah/downloads/drops/1.0.1/stable/]

-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

In the Helios (or Galileo) folder you will find the following files/folders:

- 'build' folder:
a) build.xml
Main build file. The SVN URL to checkout is defined here, in the 'init' target, inside 'svn' task.

b) customTargets.xml
Auxiliary build file. The signing process and update site copy are defined here,
in the 'post build' target.

c) build.properties
Contains some definitions, like the feature to be built, build label, eclipse location, etc.

d) repos folder (should be copied from org.eclipse.sequoyah.releng)
This folder contains the repositories to compile against.

e) transformed folder (should be copied from org.eclipse.sequoyah.releng)  
This folder contains the above mentioned repositories, transformed in a usable format.

- 'antcontrib' and 'svnant' folders:
Contain the libraries that are used inside the build scripts and should be kept.

- 'eclipse' folder:
This is the standart place for the eclipse files.

-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

If you have any questions/criticisms/suggestions, please use our mailing-list:
	sequoyah-dev@eclipse.org
	
-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-	