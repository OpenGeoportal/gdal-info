package org.opengeoportal.gdal;

import java.util.HashMap;
import java.util.Map;

import org.opengeoportal.gdal.GdalInquirerImpl.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
* This is just a little servlet that takes a query string param named
* "file_path". This parameter should be the whole directory and file
* name string below the ImageCollection directory name (this is the
* name identifying the Store in Geoserver). It returns a JSON string
* which defines an object that has four properties: 'minx', 'miny',
* 'maxx', 'maxy'. In the gdal.properties, you can set two configuration
* parameters. One, imagesDir, is the absolute path of the
* ImageCollection directory with a trailing slash. The other,
* gdalInfoPath, is the absolute path of the gdalinfo command.
* 
* This servlet is called from nonGeoreferencedBerkeley.js. By returning
* the image's size, it allows OpenLayers to be started with the image
* more or less centered and big enough to see.
* 
* @author Garey Mills, Chris Barnett
*/

@Controller
@RequestMapping("/gss")
public class GdalSizeInfoController {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	GdalInquirer gdalInquirer;
	
	@RequestMapping(method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Map<String,Double> processGS(@RequestParam("file_path") String path) throws Exception {

	    //What we want here is to construct a json string that contains
	    //minx, miny, maxx, maxy with y negative
	    
	    double minx = 0.0;
	    double miny = -65532.0;
	    double maxx = 65532.0;
	    double maxy = 0.0;
	    
	    try {
	    	Size size = gdalInquirer.getSizeInfo(path);
	    	miny = -1 * size.y;
	    	maxx = size.x;
	    } catch (Exception e){
	    	logger.error("Error retrieving size info from GDAL about '" + path + ";");
	    }
	    
		Map<String, Double> responseMap = new HashMap<String, Double>();
		responseMap.put("minx", minx);
		responseMap.put("miny", miny);
		responseMap.put("maxx", maxx);
		responseMap.put("maxy", maxy);

		return responseMap;
		  
	}
	
}
