package org.opengeoportal.gdal;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opengeoportal.gdal.GdalInquirerImpl.Size;

public interface GdalInquirer {
	Size getSizeInfo(String path) throws Exception;
	Matcher extractGdalInfo(Pattern pattern, String gdalinfoArgs) throws IOException, InterruptedException;
}
