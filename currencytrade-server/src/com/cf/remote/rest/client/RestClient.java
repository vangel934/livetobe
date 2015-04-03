package com.cf.remote.rest.client;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.uri.UriTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cf.model.Fx;
import com.cf.processor.impl.PropertyReader;
import com.cf.remote.rest.impl.GsonProvider;

public class RestClient {

	private static class ClassIntrospection {

		static Method findMethodInClass(String sMethodName,
				Class<? extends Annotation> oAnnotationClazz, Class<?> oClazz,
				Class<?>... oParamClasses) {
			for (Method oMethod : oClazz.getMethods()) {
				if (ClassIntrospection.isMethodEligible(oMethod, sMethodName,
						oAnnotationClazz)) {
					return oMethod;
				}
			}

			for (Method oMethod : oClazz.getDeclaredMethods()) {
				if (ClassIntrospection.isMethodEligible(oMethod, sMethodName,
						oAnnotationClazz)) {
					return oMethod;
				}
			}

			try {
				return oClazz.getMethod(sMethodName, oParamClasses);
			} catch (NoSuchMethodException e) {
				return null;

			}

		}

		private static boolean isMethodEligible(Method oMethod,
				String sMethodName, Class<? extends Annotation> oAnnotationClazz) {
			return oMethod.getName().equals(sMethodName)
					&& (oAnnotationClazz != null ? oMethod
							.getAnnotation(oAnnotationClazz) != null : true);
		}
	}

	private static final String DEFAULT_PROTOCOL = "http";

	private static final String TEMPLATE = "{server.protocol}://{server.host}:{server.port}/{rest.app}/{rest.context}/{rest.service}";

	private static final String PROP_SERVERHOST = "server.host";
	private static final String PROP_SERVERPORT = "server.port";
	private static final String PROP_SERVERPROTOCOL = "server.protocol";
	private static final String PROP_RESTAPP = "rest.app";
	private static final String PROP_RESTCONTEXT = "rest.context";
	private static final String PROP_RESTSERVICE = "rest.service";

	private String serverProtocol;
	private String serverHost;
	private String serverPort;
	private String restApp;
	private String context;
	private String serviceName;

	private volatile WebTarget service;
	private Class<?> serviceClass;
	private static final Object LOCK = new Object();

	private Logger log = LoggerFactory.getLogger(getClass());

	// private String getTemplate() {
	// return MessageFormat.format(TEMPLATE, PROP_SERVERPROTOCOL,
	// PROP_SERVERPORT, PROP_SERVERHOST, PROP_RESTAPP,
	// PROP_RESTCONTEXT, PROP_RESTSERVICE);
	// }

	public RestClient(Class<?> serviceClass) {
		this(null, null, null, null, null, null, serviceClass);
	}

	public RestClient(String serverProtocol, String serverHost,
			String serverPort, String restApp, String context,
			String serviceName, Class<?> serviceClass) {
		setServerProtocol(serverProtocol);
		setServerHost(serverHost);
		setServerPort(serverPort);
		setRestApp(restApp);
		setContext(context);
		setServiceName(serviceName);
		this.serviceClass = serviceClass;
	}

	/**
	 * Returns method URI. Tries to look for method URI pattern in @Path
	 * annotation either in interface or in implementation class itself. Then it
	 * applies provided map of path parameters on this pattern and returns final
	 * URI. If it cannot find either annotation or method based on name it will
	 * treat provided method name as method URI pattern. If path parameters are
	 * not provided, it will treat method pattern as final method URI.
	 *
	 * @param sMethodName
	 *            - either method name, or method URI pattern
	 * @param mapPathParams
	 * @return
	 */
	protected String getMethodUri(String methodName,
			Map<String, String> pathParams) {
		/*
		 * get remote method pattern from @Path annotation. If annotation is not
		 * present, returns method name.
		 */
		String sMethodPattern = getRemoteMethod(methodName);

		/* if annotation or method cannot be found, treat method name as pattern */
		if (sMethodPattern == null) {
			sMethodPattern = methodName;
		}

		/*
		 * apply URI creation only if path parameters exist, otherwise treat
		 * method pattern as final method URI
		 */
		if (pathParams != null) {
			UriTemplate oTemplate = new UriTemplate(sMethodPattern);
			return oTemplate.createURI(pathParams);
		} else {
			return sMethodPattern;
		}

	}

	private Method findServiceMethod(String methodName, Class<?> clazz,
			Class<?>... methodParam) {
		/* first look for method in current class/interface */
		Method method = ClassIntrospection.findMethodInClass(methodName,
				Path.class, clazz, methodParam);
		/*
		 * if it cannot be found in current class/interface, look in directly
		 * implemented interfaces
		 */
		if (method == null) {
			for (Class<?> oInterface : clazz.getInterfaces()) {
				if (method == null) {
					method = findServiceMethod(methodName, oInterface,
							methodParam);
				}
			}
		}
		/*
		 * if it cannot be found not even in directly implemented interfaces,
		 * look at possible superclass(abstract or normal)
		 */
		if (method == null && !clazz.isInterface()) {
			Class<?> oSuperclass = clazz.getSuperclass();
			// stop at Object class
			if (oSuperclass != null && oSuperclass != Object.class) {
				method = findServiceMethod(methodName, oSuperclass, methodParam);
			}
		}

		return method;
	}

	protected String getRemoteMethod(String methodName, Class<?>... methodParam) {

		if (methodName == null) {
			return null;
		}

		String remoteMethodName = null;

		try {
			Method remoteMethod = null;
			if (serviceClass != null) {
				remoteMethod = findServiceMethod(methodName, serviceClass,
						methodParam);

				if (remoteMethod != null) {
					Path oRemotePath = remoteMethod.getAnnotation(Path.class);
					if (oRemotePath != null) {
						remoteMethodName = oRemotePath.value();
					}
				}
			}

		} catch (SecurityException e) {
			log.warn("Using default name:" + methodName);
			if (log.isTraceEnabled()) {
				e.printStackTrace();
			}
		}

		if (log.isTraceEnabled()) {
			log.trace("Using default name:" + methodName);
		}

		if (remoteMethodName == null) {
			remoteMethodName = methodName;
		}

		return remoteMethodName;
	}

	protected <T, S> T executeMethod(String methodName, String user,
			String pass, Map<String, Object> queryParams,
			Map<String, String> pathParams, S request, Class<T> responseClass) {
		if (log.isDebugEnabled()) {
			log.debug("::{} user:{} ", methodName, user);
		}
		if (log.isTraceEnabled()) {
			log.trace("parameters: {}", pathParams);
		}

		WebTarget webTarget = methodName != null ? getService().path(
				getMethodUri(methodName, pathParams)) : getService();

		/* add query params */
		if (queryParams != null) {
			for (Entry<String, Object> entry : queryParams.entrySet()) {
				webTarget = webTarget.queryParam(entry.getKey(),
						entry.getValue());
			}
		}

		Builder oBuilder = webTarget.request();

		/* add auth-user header if needed */
		if (user != null) {
			oBuilder.header("auth-user", user);
		}
		/* add auth-pass header if needed */
		if (pass != null) {
			oBuilder.header("auth-pass", pass);
		}

		/* make HTTP call on builder */
		T response = oBuilder.post(
				Entity.entity(request, MediaType.APPLICATION_JSON_TYPE),
				responseClass);

		return response;
	}

	public WebTarget getService() {
		if (service == null) {
			synchronized (LOCK) {
				if (service == null) {
					ClientConfig config = new ClientConfig();
					config.register(new GsonProvider<Fx>());
					Client client = ClientBuilder.newClient(config);
					service = client.target(getBaseURI());
				}
			}
		}

		return service;
	}

	private URI getBaseURI() {
		UriTemplate template = new UriTemplate(TEMPLATE);
		return UriBuilder.fromUri(template.createURI(getConfigParams()))
				.build();
	}

	private Map<String, String> getConfigParams() {
		Map<String, String> mapParams = new HashMap<>();
		mapParams.put(PROP_SERVERHOST, getServerHost());
		mapParams.put(PROP_SERVERPORT, getServerPort());
		mapParams.put(PROP_SERVERPROTOCOL, getServerProtocol());
		mapParams.put(PROP_RESTAPP, getRestApp());
		mapParams.put(PROP_RESTCONTEXT, getContext());
		mapParams.put(PROP_RESTSERVICE, getServiceName());

		return mapParams;
	}

	public String getServerProtocol() {
		return serverProtocol;
	}

	public void setServerProtocol(String serverProtocol) {
		if (serverProtocol == null) {
			serverProtocol = System.getProperty(PROP_SERVERPROTOCOL,
					PropertyReader.INSTANCE.readProperty(PROP_SERVERPROTOCOL,
							DEFAULT_PROTOCOL));
		}
		this.serverProtocol = serverProtocol;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		if (serverHost == null) {
			serverHost = System.getProperty(PROP_SERVERHOST,
					PropertyReader.INSTANCE.readProperty(PROP_SERVERHOST));
		}
		this.serverHost = serverHost;
	}

	public String getServerPort() {

		return serverPort;
	}

	public void setServerPort(String serverPort) {
		if (serverPort == null) {
			serverPort = System.getProperty(PROP_SERVERPORT,
					PropertyReader.INSTANCE.readProperty(PROP_SERVERPORT));
		}
		this.serverPort = serverPort;
	}

	public String getRestApp() {
		return restApp;
	}

	public void setRestApp(String restApp) {
		if (restApp == null) {
			restApp = System.getProperty(PROP_RESTAPP,
					PropertyReader.INSTANCE.readProperty(PROP_RESTAPP));
		}
		this.restApp = restApp;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		if (context == null) {
			context = System.getProperty(PROP_RESTCONTEXT,
					PropertyReader.INSTANCE.readProperty(PROP_RESTCONTEXT));
		}
		this.context = context;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		if (serviceName == null) {
			serviceName = System.getProperty(PROP_RESTSERVICE,
					PropertyReader.INSTANCE.readProperty(PROP_RESTSERVICE));
		}
		this.serviceName = serviceName;
	}

}
