package org.n52.sos.binding.rest.resources.observations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedchObservationsHelpers {

	public static float[] coordToArray(String coord) {
		String[] s = coord.split(" ");
		float[] array = { Float.parseFloat(s[0]), Float.parseFloat(s[1]) };
		return array;
	}

	public static String getGmlPoint(String xmlText) {
		String gmlPosPattern = "<gml:pos.*>(.+?)</gml:pos>";
		Pattern pattern = Pattern.compile(gmlPosPattern);
		Matcher matcher = pattern.matcher(xmlText);
		matcher.find();
		return matcher.group(1);
	}

	public static Map<?, ?> propertiesToMap(File propsFile) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		
		InputStream inStream = new FileInputStream(propsFile);
		Properties props = new Properties();
		props.load(inStream);
		
		for (String key : props.stringPropertyNames()) {
		    map.put(key, props.getProperty(key));
		}
		
		return props;
	}
}
