package org.OpenGeoPortal.gdal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.OpenGeoPortal.Utilities.LogToFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
//Sample GDAL info responses
 
Driver: MrSID/Multi-resolution Seamless Image Database (MrSID)
Files: 17076013_06_062a_s.sid
Size is 6260, 8740
Coordinate System is `'
Origin = (-0.001666666666667,29.131666666666664)
Pixel Size = (0.003333333333333,-0.003333333333333)
Metadata:
  ICC__Profile=
  IMAGE__COMPRESSION_BLOCK_SIZE=512
  IMAGE__COMPRESSION_GAMMA=2.000000
  IMAGE__COMPRESSION_NLEV=7
  IMAGE__COMPRESSION_VERSION=2,0,0
  IMAGE__COMPRESSION_WEIGHT=2.000000
  IMAGE__CREATION_DATE=Tue Jan 16 15:39:17 2007

  IMAGE__ENCODING_APPLICATION=GeoExpress 6.0.0.1331
  IMAGE__INPUT_FILE_SIZE=164156076.000000
  IMAGE__INPUT_FORMAT=TIFF
  IMAGE__INPUT_NAME=H:\Furt tiffs spiffed\17076013_06_062a.tif
  IMAGE__TARGET_COMPRESSION_RATIO=14.999999
  PShop__ImageResources=
  VERSION=MG2
Image Structure Metadata:
  INTERLEAVE=PIXEL
Corner Coordinates:
Upper Left  (  -0.0016667,  29.1316667)
Lower Left  (  -0.0016667,  -0.0016667)
Upper Right (  20.8650000,  29.1316667)
Lower Right (  20.8650000,  -0.0016667)
Center      (  10.4316667,  14.5650000)
Band 1 Block=1024x128 Type=Byte, ColorInterp=Red
  Minimum=25.000, Maximum=251.000, Mean=215.362, StdDev=43.562
  Overviews: 3130x4370, 1565x2185, 783x1093, 392x547, 196x274, 98x137, 49x69
Band 2 Block=1024x128 Type=Byte, ColorInterp=Green
  Minimum=21.000, Maximum=255.000, Mean=212.999, StdDev=43.338
  Overviews: 3130x4370, 1565x2185, 783x1093, 392x547, 196x274, 98x137, 49x69
Band 3 Block=1024x128 Type=Byte, ColorInterp=Blue
  Minimum=32.000, Maximum=249.000, Mean=204.221, StdDev=41.688
  Overviews: 3130x4370, 1565x2185, 783x1093, 392x547, 196x274, 98x137, 49x69



Driver: GTiff/GeoTIFF
Files: 17076013_06_055a.tif
Size is 6359, 8764
Coordinate System is `'
Metadata:
  TIFFTAG_IMAGEDESCRIPTION=17076013_01_001a, 4/15/06, 9:08 AM,  8C, 6422x8806 (978+1009), 100%, g18crve 2,  1/30 s, R44.4, G31.8, B61.1
  TIFFTAG_SOFTWARE=Adobe Photoshop 7.0
  TIFFTAG_DATETIME=2006:04:15 09:20:14
  TIFFTAG_XRESOLUTION=300
  TIFFTAG_YRESOLUTION=300
  TIFFTAG_RESOLUTIONUNIT=2 (pixels/inch)
Image Structure Metadata:
  INTERLEAVE=PIXEL
Corner Coordinates:
Upper Left  (    0.0,    0.0)
Lower Left  (    0.0, 8764.0)
Upper Right ( 6359.0,    0.0)
Lower Right ( 6359.0, 8764.0)
Center      ( 3179.5, 4382.0)
Band 1 Block=6359x1 Type=Byte, ColorInterp=Red
Band 2 Block=6359x1 Type=Byte, ColorInterp=Green
Band 3 Block=6359x1 Type=Byte, ColorInterp=Blue

*/

/**
 * @author Garey Mills, Chris Barnett
 *
 * Use GDAL via the 'command line' to get info about an image.  
 */
public class GdalInquirerImpl implements GdalInquirer {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	String logFilePath = null;
	LogToFile fileLog = null;
    String imagesDir = null;
    String gdalInfoPath = null;
    private Runtime rt = null;
    private Pattern sizeP = null;
    private final String sizePString = "Size is (\\d*), (\\d*)";

    public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	public String getImagesDir() {
		return imagesDir;
	}

	public void setImagesDir(String imagesDir) {
		this.imagesDir = imagesDir;
	}

	public String getGdalInfoPath() {
		return gdalInfoPath;
	}

	public void setGdalInfoPath(String gdalInfoPath) {
		this.gdalInfoPath = gdalInfoPath;
	}


	//an object to hold our size info response
    protected class Size {
    	Size(Double x, Double y){
    		this.x = x;
    		this.y = y;
    	}
    	
    	Double x; 
    	Double y;
    }
    
    private Runtime getRuntime(){
    	if (rt == null){
    		rt = Runtime.getRuntime();
    	}
    	return rt;
    }
    
    private Pattern compilePattern(String patternString){
    	Pattern pattern = null;
    	try{
    		pattern = Pattern.compile(patternString);
    	} catch (PatternSyntaxException e){
    		logger.error("Error compiling RegEx Pattern ['" + patternString + "']" );
    	}
    	return pattern;
    }

    public Matcher extractGdalInfo(Pattern pattern, String gdalinfoArgs) throws IOException, InterruptedException {
	    Process p = getRuntime().exec(gdalInfoPath + " " + gdalinfoArgs);
	    
	    BufferedReader ebr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    
	    String msg;
	    StringBuffer gdal_return = new StringBuffer();
	    
	    p.waitFor();
	    
	    if(p.exitValue() != 0) {
	    	//info from the runtime stderr
	    	if (fileLog == null){
	        	fileLog  = new LogToFile(logFilePath);
	    	}
	    	fileLog.log("gdalinfo failure for " + gdalinfoArgs);
	    	while((msg = ebr.readLine()) != null) { logger.error(msg); }
	    } else {
	    	//info from the runtime stdout
	    	while((msg = br.readLine()) != null) { gdal_return.append(msg); }
	    }
	    
	    ebr.close();
	    br.close();
	    
	    String gdalS = gdal_return.toString();
	    
	    //Use regex to match text in the response
	    Matcher matcher = pattern.matcher(gdalS);
	    return matcher;
    }
    
    
	public Size getSizeInfo(String path) throws Exception{
		if (sizeP == null){
			sizeP = compilePattern(sizePString);
		}

	    String args = imagesDir + "/" + path;
	    Matcher sizeM = extractGdalInfo(sizeP, args);

	    
	    if(sizeM.find()) {
	    	return new Size(Double.parseDouble(sizeM.group(2)), Double.parseDouble(sizeM.group(1)));
	    } else {
	    	throw new Exception("GDAL returned no size info about this image.");
	    }

	}
}
