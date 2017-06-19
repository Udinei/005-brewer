package com.algaworks.brewer.base;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

/**
 * Essa classe e responsavel por conectar o DBUnit no banco, aplicar as inserçoes do arquivo BrewerXmlDBData.xml
 * na base de dados e desfazer as alteções apos os testes. 
 * Inserir a linha de codigo abaixo no metodo init() das classes de testes para desafazer as alterações dos testes no banco
 * dbUnitHelper.execute(DatabaseOperation.CLEAN_INSERT, "BrewerXmlDBData.xml");
 **/

public class DbUnitHelper {

	private Connection conexao;
	private DatabaseConnection conexaoDBUnit;
	private String xmlFolder;
	private List<String> infoBD = null;
	private String querySql = "";
	
	
	public DbUnitHelper(List<String> infoBD, String xmlFolder, String querySql) {
	    this.infoBD = infoBD;
	    this.xmlFolder = xmlFolder;
	    this.querySql = querySql;
	}

	
	
	public void conectaBD() {

		try {
			Class.forName(getDriverBD()).newInstance();
			conexao = DriverManager.getConnection(getUrlBD(), getUsername(), getPassword());
			
			conexaoDBUnit = new DatabaseConnection(conexao);
			DatabaseConfig config = conexaoDBUnit.getConfig();
			
			// permite que campos com valores null ou vazios ("") sejam utilizados no arquivo da base BrewerXmlBDData.xml 
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
			
			// executa o sql caso o teste informe algum
			executeSql();
			
			operation.execute(conexaoDBUnit, dataSet);
			
		} catch (Exception e) {
			throw new RuntimeException("Erro executando DbUnit", e);
		}
	}


	/** Esse metodo executa um SQL na base de dados antes de executar os testes, a classe informa o sql*/
	private void executeSql() throws SQLException {
		if(!querySql.isEmpty()){
		   conexaoDBUnit.getConnection().prepareStatement(querySql).execute();
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

	public String getDriverBD(){
		return infoBD.get(0);
	}
	
	public String getUrlBD(){
		return infoBD.get(1);
	}
	
	public String getUsername(){
		return infoBD.get(2);
	}

	public String getPassword(){
		return infoBD.get(3);
	}


}