package xx;

import java.util.Collection;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import io.shardingsphere.api.config.strategy.ShardingStrategyConfiguration;

public class MyDatabaseShardingStrategy implements PreciseShardingAlgorithm{

	@Override
	public String doSharding(Collection availableTargetNames, PreciseShardingValue shardingValue) {
		// TODO Auto-generated method stub
		System.out.println(availableTargetNames);
		System.out.println(shardingValue);
		String last= shardingValue.getValue().toString().replace("-", "");
		for(Object str:availableTargetNames){
			if(str.toString().endsWith(last)) return str.toString();
		}
		return null;
	}

}
