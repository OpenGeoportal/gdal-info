package org.OpenGeoPortal.gdal;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.OpenGeoPortal.gdal.GdalInquirerImpl.Size;

public interface GdalInquirer {
	Size getSizeInfo(String path) throws Exception;
	Matcher extractGdalInfo(Pattern pattern, String gdalinfoArgs) throws IOException, InterruptedException;
}
