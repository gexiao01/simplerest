package com.dp.simplerest.binding;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.LocalVariableAttribute;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.dp.simplerest.annotation.Rest;
import com.dp.simplerest.exception.RestException;
import com.dp.simplerest.util.JsonUtils;

/**
 * A simple implement of annotation based request mapping
 * 
 * @author xiao.ge
 * @date 20151204
 */
@Component
public class BindingMeta implements ApplicationContextAware {

	// key-url,value-method with #@Rest annotation
	private Map<String, Method> apiMap;
	// key-apiMap.value.getClass(), value-a Singleton instance for relect
	// invokation
	private Map<Class<?>, Object> beanMap;
	private Map<String, String[]> paramNamesMap;

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * scan all beans and extract #@Rest metas
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		logger.info("binging REST meta");

		apiMap = new HashMap<String, Method>();
		beanMap = new HashMap<Class<?>, Object>();
		paramNamesMap = new HashMap<String, String[]>();

		Map<String, Object> allBeans = applicationContext.getBeansWithAnnotation(Rest.class);
		for (Object instance : allBeans.values()) {
			Class<?> clz = instance.getClass();
			beanMap.put(clz, instance);
			parseMethods(clz);
		}

	}

	private void parseMethods(Class<?> clz) {

		for (Method method : clz.getDeclaredMethods()) {
			Rest rest = method.getAnnotation(Rest.class);
			if (rest != null) {
				logger.info("Binding url " + rest.path() + " to " + method);
				apiMap.put(rest.path(), method);
				String[] paramNames = parseParamNames(method); // param names
				paramNamesMap.put(rest.path(), paramNames);
			}
		}
	}

	/**
	 * mapping request paramaters and invoke respective method
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Object invoke(HttpRequest request) throws RestException {

		String uri = request.getUri();
		int index = uri.indexOf('?');
		String path = index >= 0 ? uri.substring(0, index) : uri;
		Method method = apiMap.get(path);

		if (method == null) {
			throw new RestException("No method binded for request " + path);
		}
		try {
			Class<?>[] argClzs = method.getParameterTypes(); // Classes for args
			Object[] args = new Object[argClzs.length]; // parsed params
			String[] paramNames = paramNamesMap.get(path);

			Map<String, List<String>> requestParams = new QueryStringDecoder(uri).getParameters();

			for (int i = 0; i < argClzs.length; i++) {
				Class<?> argClz = argClzs[i];
				String paramName = paramNames[i];
				if (!requestParams.containsKey(paramName) || CollectionUtils.isEmpty(requestParams.get(paramName))) {
					// not param of paramName
					// TODO add more annotations like @Required or
					// @DefaultValue, etc.
					args[i] = null;
					continue;
				}

				String param = requestParams.get(paramNames[i]).get(0);
				if (param == null) {
					args[i] = null;
				} else if (argClz == HttpRequest.class) {
					args[i] = request;
				} else if (argClz == long.class || argClz == Long.class) {
					args[i] = Long.valueOf(param);
				} else if (argClz == int.class || argClz == Integer.class) {
					args[i] = Integer.valueOf(param);
				} else if (argClz == boolean.class || argClz == Boolean.class) {
					args[i] = Boolean.valueOf(param);
				} else if (argClz == String.class) {
					args[i] = param;
					// TODO add here if other type binding needed
				} else {
					try { // try a json deserialization
						args[i] = JsonUtils.fromJsonString(argClz, param);
					} catch (Exception e) {
						args[i] = null;
					}
				}
			}

			Object instance = beanMap.get(method.getDeclaringClass());
			return method.invoke(instance, args);
		} catch (Exception e) {
			throw new RestException(e);
		}
	}

	private String[] parseParamNames(Method method) {
		try {
			CtMethod cm = ClassPool.getDefault().get(method.getDeclaringClass().getName())
					.getDeclaredMethod(method.getName());
			LocalVariableAttribute attr = (LocalVariableAttribute) cm.getMethodInfo().getCodeAttribute()
					.getAttribute(LocalVariableAttribute.tag);

			String[] paramNames = new String[cm.getParameterTypes().length];
			int offset = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;

			for (int i = 0; i < cm.getParameterTypes().length; i++) {
				paramNames[i] = attr.variableName(i + offset);
			}
			return paramNames;
		} catch (NotFoundException e) {
			logger.error("parseParamNames Error", e);
			return new String[] {};
		}
	}

	public Map<String, Method> getApiMap() {
		return apiMap;
	}

}
