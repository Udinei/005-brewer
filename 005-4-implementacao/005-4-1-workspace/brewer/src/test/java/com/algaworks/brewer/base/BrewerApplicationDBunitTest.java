package com.algaworks.brewer.base;

import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.dbunit.DBTestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;


@EnableTransactionManagement
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
@RunWith(SpringRunner.class)
@TestPropertySource(locations="classpath:application.properties")
@DatabaseSetup("/META-INF/sampleData.xml")
@SpringBootTest
public class BrewerApplicationDBunitTest extends DBTestCase {
    

	
	@Test
	public void contextLoads() {
		
	}

	
	/**
	 * This is the underlying BasicDataSource used by Dao. If The Dao is using a
	 * support class from Spring (i.e. HibernateDaoSupport) this is the
	 * BasicDataSource that is used by Spring.
	 */
	//@Autowired
	private BasicDataSource dataSource;
	
		
	public void setDatasource(){
	    dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost/brewer?useSSL=false");
		dataSource.setUsername("root");
		dataSource.setPassword("root");	
	}
	
	/**
	 * DBUnit specific object to provide configuration to to properly state the
	 * underlying database
	 */
	private IDatabaseTester databaseTester;

	
	/**
	 * Retrieve the DataSet to be used from Xml file. This Xml file should be
	 * located on the classpath.
	 */
	@Override
	protected IDataSet getDataSet() throws Exception {
	    final FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
	    builder.setColumnSensing(true);
	    return builder.build(this.getClass().getClassLoader()
	            .getResourceAsStream("/META-INF/sampleData.xml"));
	}
	
	/**
	 * Perform any required database clean up after the test runs to ensure the
	 * stale state has not been dirtied for the next test.
	 * 
	 * @throws java.lang.Exception
	 */

	@After
	public void tearDown() throws Exception {
	    // databaseTester.setTearDownOperation(this.getTearDownOperation());
	   // databaseTester.onTearDown();
	    
	    
	}


	/**
	 * On setUp() refresh the database updating the data to the data in the
	 * stale state. Cannot currently use CLEAN_INSERT due to foreign key
	 * constraints.
	 */
	@Override
	protected DatabaseOperation getSetUpOperation() {
	    return DatabaseOperation.DELETE_ALL;
	}

	/**
	 * On tearDown() truncate the table bringing it back to the state it was in
	 * before the tests started.
	 */
	@Override
	protected DatabaseOperation getTearDownOperation() {
	    return DatabaseOperation.TRUNCATE_TABLE;
	}

	/**
	 * Overridden to disable the closing of the connection for every test.
	 * @throws SQLException 
	 */
	@Override
	protected void closeConnection(IDatabaseConnection conn) throws SQLException {
	    //conn.close(); // Empty body on purpose.
	}
	

}
