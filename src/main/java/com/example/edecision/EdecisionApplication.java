package com.example.edecision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "com.example.edecision")
@EnableSwagger2
public class EdecisionApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdecisionApplication.class, args);
	}

	@Bean
	public Docket apis(){
		// return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com.example.edecision")).build();

		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(
						new ApiInfoBuilder()
								.title("E-decision")
								.description("Documentation technique de l'API pour l'application E-decision")
								.license("Hugo Lorent - Olivier Leperlier")
								.version("1.0")
								.build()
				)
				.groupName("ipi-m2il")
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.example.edecision"))
				.paths(PathSelectors.any())
				.build();
	}

}
