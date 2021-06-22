package org.reactome.server.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.Aspects;
import org.reactome.server.graph.aop.LazyFetchAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@PropertySource("classpath:service.properties")
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // doesn't help below
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);

        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.valueOf("text/plain; charset=UTF-8"));
        mediaTypes.add(MediaType.valueOf("application/json; charset=UTF-8"));
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        stringHttpMessageConverter.setSupportedMediaTypes(mediaTypes);

        converters.add(mappingJackson2HttpMessageConverter);
        converters.add(stringHttpMessageConverter);
    }

    @Bean
    public void CommonsMultipartResolver(){
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(2000000);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("/resources/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/upload").setViewName("uploadlog");
    }

    /**
     * This is needed to get hold of the instance of the aspect which is created outside of the spring container,
     * and make it available for autowiring.
     */
    @Bean
    public LazyFetchAspect lazyFetchAspect() {
        return Aspects.aspectOf(LazyFetchAspect.class);
    }
}
