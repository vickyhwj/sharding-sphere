package xx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.datanucleus.store.rdbms.datasource.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import io.shardingsphere.api.config.rule.ShardingRuleConfiguration;
import io.shardingsphere.api.config.rule.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingsphere.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
public class Main {
	static String getActualDataNodes(){
		String dbStr="";
		for(int i=1;i<=12;++i){
			if(i<10){
				dbStr=dbStr+"db20170"+i+".record,";
			}else{
				dbStr=dbStr+"db2017"+i+".record,";	
			}
		}
		return dbStr.substring(0, dbStr.length()-1);
	}
	static Map<String,DataSource> createDateSourceMap(){
		Map<String,DataSource> dataSourceMap=new HashMap<>();
		for(int i=1;i<=12;++i){
			BasicDataSource dataSource1 = new BasicDataSource();
		    dataSource1.setDriverClassName("com.mysql.jdbc.Driver");
		    dataSource1.setUrl("jdbc:mysql://localhost:3306/db2017"+(i<10? "0"+i:""+i)+"?useUnicode=true&characterEncoding=UTF-8");
		    dataSource1.setUsername("root");
		    dataSource1.setPassword("root");
		    dataSourceMap.put("db2017"+(i<10? "0"+i:""+i), dataSource1);
		}
		return dataSourceMap;
	}
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		// 配置真实数据源
	    // 配置第一个数据源
	  
	  
	    
	    // 配置Order表规则
	    TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
	    orderTableRuleConfig.setLogicTable("record");
	    orderTableRuleConfig.setActualDataNodes(getActualDataNodes());
	    
	    
	 // 自定义的分片算法实现
        StandardShardingStrategyConfiguration standardStrategy = new StandardShardingStrategyConfiguration("mon_key", new MyDatabaseShardingStrategy());
	    // 配置分库 + 分表策略
	    orderTableRuleConfig.setDatabaseShardingStrategyConfig(standardStrategy);
	    
	    // 配置分片规则
	    ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
	    shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);
	    
	    // 省略配置order_item表规则...
	    // ...
	    
	    // 获取数据源对象
	    Properties p=new Properties();
	    p.setProperty("sql.show", "true");
	    DataSource dataSource = ShardingDataSourceFactory.createDataSource(createDateSourceMap(), shardingRuleConfig, new ConcurrentHashMap(), p);
	
	    final JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
	    
//	    Connection connection= dataSource.getConnection();
//	  //  ResultSet rs= connection.createStatement().executeQuery("select * from record where mon_key='2017-01'");
//	    PreparedStatement ps= connection.prepareStatement("SELECT id,count(id) as c from record GROUP BY id having c>2");
//	 //   ps.setString(1,"2017-01" );
//	    ResultSet rs=ps.executeQuery();
//	    while(rs.next()){
//	    	System.out.println(rs.getObject(1));
//	    }
//	    
	
	    exeuteWithTime(new SqlExecuter() {
			
			@Override
			public void execute() {
				// TODO Auto-generated method stub
			    List<Map<String,Object>> result= jdbcTemplate.queryForList("SELECT id,count(id) as c from record GROUP BY id HAVING count(id) >1");
			    System.out.println(result);
				
			}
		});
//	    exeuteWithTime(new SqlExecuter() {
//			
//			@Override
//			public void execute() {
//				// TODO Auto-generated method stub
//			   jdbcTemplate.update("insert into record(auxcheck,mon_key) values(?,?)", "dasda","2017-11");
//				
//			}
//		});
//	    
	}
	static void exeuteWithTime(SqlExecuter sqlExecuter){
		long start=System.currentTimeMillis();
		sqlExecuter.execute();
		System.out.println("time used:"+(System.currentTimeMillis()-start));
	}

}
interface SqlExecuter{
	void execute();
}
