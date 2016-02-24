package nd.esp.service.lcmsdocs.app;

import com.nd.gaea.client.http.BearerAuthorizationProvider;
import com.nd.gaea.client.support.DeliverBearerAuthorizationProvider;
import com.nd.gaea.rest.config.WafWebMvcConfigurerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "nd.esp.service.lcmsdocs.controllers" })
@EnableAspectJAutoProxy
@EnableScheduling
// @PropertySource("classpath:config/worker.properties")
public class LCMSDocsWebConfig extends WafWebMvcConfigurerAdapter {

	private final static Logger LOG = LoggerFactory.getLogger(LCMSDocsWebConfig.class);

	// @Value("${db.driver}")
	// private String driver;
	@Autowired
	private Environment env;

	/**
	 * 加载配置属性文件
	 * 
	 * @return
	 */
	/*
	 * @Bean public PropertyPlaceholderConfigurer
	 * propertyPlaceholderConfigurer() {
	 * 
	 * PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new
	 * PropertyPlaceholderConfigurer();
	 * propertyPlaceholderConfigurer.setFileEncoding("utf-8");
	 * propertyPlaceholderConfigurer.setLocation(new
	 * ClassPathResource("config/worker.properties")); try {
	 * propertyPlaceholderConfigurer.setProperties(PropertiesLoaderUtils
	 * .loadAllProperties("config/worker.properties")); } catch (IOException e)
	 * { LOG.error("加载配置文件失败",e); e.printStackTrace(); }
	 * //propertyPlaceholderConfigurer.setLocation(new
	 * ClassPathResource("META-INF/app.properties")); return
	 * propertyPlaceholderConfigurer; }
	 */
	/**
	 * @desc:新的加载配置文件
	 * 				<p>
	 *                需要申明静态来处理,支持多个配置文件
	 *                </p>
	 * @createtime: 2015年6月29日
	 * @author: liuwx
	 * @see Environment
	 * @return
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer getPropertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		return placeholderConfigurer;
	}

	@Bean
	public ResourceBundleMessageSource getResourceBundleMessageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setUseCodeAsDefaultMessage(true);// 如果找不到属性值,则使用key作为值返回
		// source.setCacheSeconds(10);
		source.setBasenames(new String[] { "config/valid/messages" });
		source.setDefaultEncoding("utf-8");
		source.setFallbackToSystemLocale(true);
		// LOG.info(source.getMessage("model.href.value.errormsg", null,
		// Locale.SIMPLIFIED_CHINESE));
		return source;
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(30 * 1000L);
	}

	@Bean
	public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
		adapter.setSynchronizeOnSession(true);
		List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
		// argumentResolvers.add(new MethodArgumentsLengthResolver());
		adapter.setCustomArgumentResolvers(argumentResolvers);

		return adapter;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		// registry.addWebRequestInterceptor()
	}

	@Bean
	@Primary
	public BearerAuthorizationProvider bearerAuthorizationProvider() {
		return new DeliverBearerAuthorizationProvider();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
		registry.addResourceHandler("/api-docs/**").addResourceLocations("/api-docs/");
	}

}
