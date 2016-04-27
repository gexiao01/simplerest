package com.dp.simplerest.binding;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.simplerest.BaseTest;
import com.dp.simplerest.binding.BindingMeta;

public class BindingMetaTest extends BaseTest {

	@Autowired
	private BindingMeta meta;

	@Test
	public void testBinding() throws Exception {

		Map<String, Method> apiMap = meta.getApiMap();
		logger.info("testBinding apiMap=" + apiMap);
		Assert.assertNotNull(apiMap.get("/test"));
		Assert.assertNotNull(apiMap.get("/test/query"));
		Assert.assertNotNull(apiMap.get("/test/insert"));
	}
}
