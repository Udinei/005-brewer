package com.algaworks.brewer.base;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.poi.util.SystemOutLogger;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

public class DbUnitHelper {

	private Connection conexao;
	private DatabaseConnection conexaoDBUnit;
	private String xmlFolder;

	public DbUnitHelper(String xmlFolder) {
		this.xmlFolder = xmlFolder;

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conexao = DriverManager.getConnection("jdbc:mysql://localhost/brewer?useSSL=false", "root", "root");
			conexaoDBUnit = new DatabaseConnection(conexao);
			DatabaseConfig config = conexaoDBUnit.getConfig();
			
			
			config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
			config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
			
		} catch (Exception e) {
			throw new RuntimeException("Erro inicializando DBUnit", e);
		}
	}

	public void execute(DatabaseOperation operation, String xml) {
		try {
			InputStream is = getClass().getResourceAsStream("/" + xmlFolder + "/" + xml);
			FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
			IDataSet dataSet = builder.build(is);
			System.out.println(">>> " +dataSet.toString());
			operation.execute(conexaoDBUnit, dataSet);
			
		} catch (Exception e) {
			throw new RuntimeException("Erro executando DbUnit", e);
		}
	}

	public void close() {
		try {
			conexaoDBUnit.close();
			conexao.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}