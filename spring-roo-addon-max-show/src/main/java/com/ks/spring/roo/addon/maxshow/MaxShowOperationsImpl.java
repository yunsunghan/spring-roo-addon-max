package com.ks.spring.roo.addon.maxshow;

import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.classpath.PhysicalTypeMetadataProvider;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.shell.Shell;
import org.springframework.roo.support.logging.HandlerUtils;

/**
 * Implementation of operations this add-on offers.
 *
 * @since 1.1
 */
@Component // Use these Apache Felix annotations to register your commands class in the Roo container
@Service
public class MaxShowOperationsImpl implements MaxShowOperations {
	
	/**
	 * MetadataService offers access to Roo's metadata model, use it to retrieve any available metadata by its MID
	 */
	@Reference private MetadataService metadataService;
	
	/**
	 * Use the PhysicalTypeMetadataProvider to access information about a physical type in the project
	 */
	@Reference private PhysicalTypeMetadataProvider physicalTypeMetadataProvider;
	
	/**
	 * Use ProjectOperations to install new dependencies, plugins, properties, etc into the project configuration
	 */
	@Reference private ProjectOperations projectOperations;

	/**
	 * Use TypeLocationService to find types which are annotated with a given annotation in the project
	 */
	@Reference private TypeLocationService typeLocationService;
	@Reference private Shell shell;
	private static final Logger logger = HandlerUtils.getLogger(MaxShowOperationsImpl.class);
	
	/** {@inheritDoc} */

	public boolean isCommandAvailable() {
		// 프로젝트가 생성하기전에도 command가 보여야 한다.
		return getProjectMetadata() == null;
	}

	public final ProjectMetadata getProjectMetadata() {
		return (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier());
	}
	
	/** {@inheritDoc} */
	public void start(String topLevelPackage, String projectName) {
		// MAX SHOW START
		// project
		shell.executeCommand("project --topLevelPackage " + topLevelPackage + " --projectName " + projectName);
		shell.executeCommand("persistence setup --provider HIBERNATE --database MYSQL");
		shell.executeCommand("database properties set --key database.password --value 1212");
		logger.warning("[MAX] this example password is '1212'...");
		shell.executeCommand("database properties set --key database.username --value root");
		logger.warning("[MAX] this example db account is 'root'...");
		shell.executeCommand("database properties set --key database.url --value jdbc:mysql://localhost:3306/test");
		logger.warning("[MAX] database name is 'test'...");
		// entity account
		shell.executeCommand("entity --class ~.account.domain.Account");
		shell.executeCommand("field string --fieldName loginId --notNull");
		shell.executeCommand("field string --fieldName loginPw --notNull");
		shell.executeCommand("field string --fieldName email --notNull");
		shell.executeCommand("field number --fieldName visitCount --type java.lang.Integer --min 0");
		shell.executeCommand("field date --fieldName writeDate --type java.util.Date --dateFormat SHORT");
		// max addon
		shell.executeCommand("max setup");
		shell.executeCommand("max service --class ~.account.service.AccountService --entity ~.account.domain.Account");
		shell.executeCommand("max web --class ~.account.web.AccountController --service ~.account.service.AccountService");
		// entity goods
		shell.executeCommand("entity --class ~.article.domain.Article");
		shell.executeCommand("field string --fieldName title");
		shell.executeCommand("field string --fieldName context");
		shell.executeCommand("field number --fieldName hitCount --type java.lang.Long --min 0");
		shell.executeCommand("field date --fieldName writeDate --type java.util.Date");
		// max addon
		shell.executeCommand("max service --class ~.article.service.ArticleService --entity ~.article.domain.Article");
		shell.executeCommand("max web --class ~.article.web.ArticleController --service ~.article.service.ArticleService");
		
	}
}