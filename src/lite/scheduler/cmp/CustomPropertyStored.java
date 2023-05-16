package lite.scheduler.cmp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.core.io.Resource;

public class CustomPropertyStored {

	private volatile ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();

	/**
	 * 建構子
	 * 
	 * @param resources
	 * @throws IOException
	 */
	public CustomPropertyStored(Resource[] resources) throws IOException {
		for (Resource resource : resources) {
			if (resource.exists()) {
				try (InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
					Properties properties = new Properties();
					properties.load(inputStreamReader);
					properties.forEach((key, value) -> {
						map.put(key.toString(), value.toString());
					});
				}
			}
		}
	}

	/**
	 * 取得 Properties
	 * 
	 * @return
	 */
	public Properties getProperties() {
		Properties properties = new Properties();
		properties.putAll(map);
		return properties;
	}

	public String getValue(String key) {
		return getValue(key, null);
	}

	public String getValue(String key, String defaultVal) {
		return map.getOrDefault(key, defaultVal);
	}
}
