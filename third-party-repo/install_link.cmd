goto comment
This script installs the following libraries in the System32 directory.
If you do not want to install these files in System32 directly, 
either put them on your PATH or add them to the Java classpath at runtime.

cairo.dll
cfitsio.dll
expat.dll
freexl.dll
fribidi.dll
gdal111.dll
gdalconstjni.dll
gdaljni.dll
geos.dll
geos_c.dll
hdf5.dll
hdf5_cpp.dll
hdf5_hl.dll
hdf5_hl_cpp.dll
hdfdll.dll
iconv.dll
jnetpcap.dll
libcurl.dll
libeay32.dll
libecwj2.dll
libfcgi.dll
libkea.dll
libmysql.dll
libpq.dll
libtiff.dll
libxml2.dll
lti_dsdk_9.1.dll
lti_lidar_dsdk_1.1.dll
mapserver.dll
mfhdfdll.dll
msvcp120.dll
msvcr120.dll
netcdf.dll
ogrjni.dll
openjp2.dll
osrjni.dll
proj.dll
spatialite.dll
sqlite3.dll
ssleay32.dll
szip.dll
tbb.dll
xdrdll.dll
xerces-c_2_8.dll
zlib1.dll

:comment


call COPY "%~dp0\target\windows\*.dll" C:\Windows\System32\
pause