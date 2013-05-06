gdal-info
=========

This repository builds as a jar that can be added to WEB-INF/lib of your ogp install to add a controller ("/gss") 
that retrieves the size of an image from GDAL via 'gdalinfo'.  It is designed to be used in conjunction with client side code 
that displays non-georeferenced maps via the GeoServer image collection plugin. By returning the image's size, it 
allows OpenLayers to be started with the image more or less centered and big enough to see.

The "gss" url expects the parameter "file_path", which is the extended path and filename for the image being queried.
(the base path is set in gdal.properties).  It returns a json object with properties:
minx, miny, maxx, maxy
These define a pixel bounds for the image.

If there is an error retrieving the image size, miny defaults to -65532.0 and maxx defaults to 65532.0

Paths are defined in 'gdal.properties'.

'gdalInquirer.logFilePath' is the absolute path to a log file that writes info about the 'gdalinfo' response.
'gdalInquirer.imagesDir' is the absolute path of the ImageCollection directory with a trailing slash.
'gdalInquirer.gdalInfoPath' is the absolute path to your 'gdalinfo' binary.


