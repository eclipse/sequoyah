Sequoyah Build Documentation

Files needed to the build process:

1) build.sh
This file contains the shell script that starts the build process, but does NOT copy the update site.

2) publish.sh
This file contains the shell script that starts the build process, AND copy the update site.

3) build.xml
Main build file. The SVN URL to checkout is defined here, in the 'init' target, inside 'svn' task.

4) customTargets.xml
Auxiliary build file. The signing process and update site copy are defined here, in the 'post build' target.

5) build.properties
Contains some definitions, like the feature to be built, build label, eclipse location, etc.

6) repos folder
This folder contains the repositories to compile against.

7) transformed folder  
This folder contains the above mentioned repositories, transformed in a usable format.

The update site will adopt the following policy:
a) The official update site URL will be: http://download.eclipse.org/sequoyah/updates/0.5/
b) There will be a folder with the build label inside this path (e.g., http://download.eclipse.org/sequoyah/updates/0.5/N_0.5.0_201002100839) which contains the signed build files, every time a build is successful.
c) If the publish.sh script was used, the build script will also copy the contents of this folder to the official update site.
