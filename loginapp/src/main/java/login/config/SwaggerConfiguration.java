package login.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
   
	private static final String CLIENT_ID = "my-trusted-client";
    private static final String SECRET = "123456";
	private static final String AUTH_SERVER = "http://localhost:8080";
	
	@Bean
	public Docket swaggerSpringMvcPlugin() {
		Set<String> protocolos = new HashSet<String>();
		protocolos.add("http");
		Set<String> consumes = new HashSet<String>();
		consumes.add(MediaType.APPLICATION_JSON_VALUE);
		Set<String> produces = new HashSet<String>();
		produces.add(MediaType.APPLICATION_JSON_VALUE);
		return new Docket(DocumentationType.SWAGGER_2)
           .select()
           .paths(PathSelectors.any()) // todas as classes
           .apis(RequestHandlerSelectors.basePackage("login"))
           .build()
           .apiInfo(apiInfo())
           .protocols(protocolos)
           .pathMapping("/")
           .consumes(consumes)
           .produces(produces)
           .directModelSubstitute(LocalDate.class, String.class)
           .genericModelSubstitutes(ResponseEntity.class)
           .securitySchemes(Arrays.asList(securityScheme()))
           .securityContexts(Arrays.asList(securityContext()))
           ;
   }

   private ApiInfo apiInfo() {
       return new ApiInfoBuilder()
           .title("LoginApp - kikoluiss")
           .version("1.0.0")
           .build();
   }

   private OAuth securityScheme() {

       List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
       authorizationScopeList.add(new AuthorizationScope("read", "read all"));
       authorizationScopeList.add(new AuthorizationScope("trust", "trust all"));
       authorizationScopeList.add(new AuthorizationScope("write", "access all"));

       List<GrantType> grantTypes = new ArrayList<>();
       GrantType creGrant = new ResourceOwnerPasswordCredentialsGrant(AUTH_SERVER + "/oauth/token");

       grantTypes.add(creGrant);

       return new OAuth("oauth2schema", authorizationScopeList, grantTypes);

   }

   private SecurityContext securityContext() {
       return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.any())
               .build();
   }

   private List<SecurityReference> defaultAuth() {

       final AuthorizationScope[] authorizationScopes = new AuthorizationScope[3];
       authorizationScopes[0] = new AuthorizationScope("read", "read all");
       authorizationScopes[1] = new AuthorizationScope("trust", "trust all");
       authorizationScopes[2] = new AuthorizationScope("write", "write all");

       return Collections.singletonList(new SecurityReference("oauth2schema", authorizationScopes));
   }

   @SuppressWarnings("deprecation")
   @Bean
   public SecurityConfiguration securityInfo() {
       return new SecurityConfiguration(CLIENT_ID, SECRET, "", "", "", ApiKeyVehicle.HEADER, "", " ");
   }
   
}
