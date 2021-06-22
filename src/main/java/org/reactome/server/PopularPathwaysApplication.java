package org.reactome.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@EntityScan({"org.reactome.server.graph.domain.model"})
@EnableNeo4jRepositories("org.reactome.server.graph.repository")
@SpringBootApplication(scanBasePackages = "org.reactome.server", exclude = {DataSourceAutoConfiguration.class })
public class PopularPathwaysApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(PopularPathwaysApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(PopularPathwaysApplication.class, args);
    }
}
