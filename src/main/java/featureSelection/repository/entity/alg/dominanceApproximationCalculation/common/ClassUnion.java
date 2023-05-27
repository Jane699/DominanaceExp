package featureSelection.repository.entity.alg.dominanceApproximationCalculation.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author daiYang_wu
 */
@Data
@AllArgsConstructor
public class ClassUnion {
    private Integer decisionValue;
    private boolean biggerOrEqual;
    private static Map<Boolean,Map<Integer,ClassUnion>> cache = new HashMap<>();
    @Override
    public String toString() {
        if (isBiggerOrEqual()) return ">=" + decisionValue.toString();
        else return "<=" + decisionValue.toString();
    }

//    public static ClassUnion getClassUnion(Integer decisionValue,boolean biggerOrEqual){
//
//        if (cache.get(biggerOrEqual).get(decisionValue) == null){
//        }
//    }
}
