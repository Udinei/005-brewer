package com.algaworks.brewer.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;

@Configuration
@ComponentScan(basePackageClasses = Cervejas.class)
@EnableJpaRepositories(basePackageClasses = Cervejas.class, enableDefaultTransactions = false)
@EnableTransactionManagement
public class JPAConfig {

//		
//	/** Opção de Datasource em produção - Esse metodo nao esta sendo usado - Pool de conexoes */
//	@Profile("prod")
//	@Bean
//	public DataSource dataSourceProd() throws URISyntaxException {
//		URI jdbUri = new URI(System.getenv("JAWSDB_URL")); // recurso de BD da Amazon
//
//	    String username = jdbUri.getUserInfo().split(":")[0];
//	    String password = jdbUri.getUserInfo().split(":")[1];
//	    String port = String.valueOf(jdbUri.getPort());
//	    String jdbUrl = "jdbc:mysql://" + jdbUri.getHost() + ":" + port + jdbUri.getPath();
//	    
//	    BasicDataSource dataSource = new BasicDataSource();
//	    dataSource.setUrl(jdbUrl);
//	    dataSource.setUsername(username);
//	    dataSource.setPassword(password);
//	    dataSource.setInitialSize(10);  // qtd de conexao
//	    return dataSource;
//	}
	
	// configura o JPA
	@Bean
	public JpaVendorAdapter jpaVendorAdapter(){
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.MYSQL);
		adapter.setShowSql(false); // desativa (não sera exibido em producao), exibicao do log sera controlado pelo log4j2.xml
		adapter.setGenerateDdl(false);
		adapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
		return adapter;
	}
	
	// gerencia consultas nativas 
	@Bean
	public EntityManagerFactory entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter){
	    LocalContainerEntityManagerFactoryBean factory= new LocalContainerEntityManagerFactoryBean();
	    factory.setDataSource(dataSource);
	    factory.setJpaVendorAdapter(jpaVendorAdapter);
	    factory.setPackagesToScan(Cerveja.class.getPackage().getName()); // obtem o nome do pacote onde se encontram  as entidades
	    factory.setMappingResources("sql/consultas-nativa.xml"); // local onde se encontra consultas nativas slq
	    factory.afterPropertiesSet();
	    return factory.getObject();
	}
	
	// controla as transações JPA
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
		
	}
	
	
}
