package featureSelection.repository.entity.alg.dominanceApproximationCalculation.rpda;

import featureSelection.basic.model.universe.instance.Instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author daiYang_wu
 */
public class RPDAStoreInfo {
    Map<Integer, List<Instance>> stHash;
    Map<Integer, List<Instance>> utHash;
    Map<Integer, List<Instance>> ltHash;

    public RPDAStoreInfo(int size) {
        stHash = new HashMap<>(size);
        utHash = new HashMap<>(size);
        ltHash = new HashMap<>(size);
    }


    public void initDecisionKey(int decisionValue){
        stHash.put(decisionValue, new ArrayList<>());
        utHash.put(decisionValue, new ArrayList<>());
        ltHash.put(decisionValue, new ArrayList<>());
    }


    public Map<Integer, List<Instance>> getStHash() {
        return stHash;
    }

    public void setStHash(Map<Integer, List<Instance>> stHash) {
        this.stHash = stHash;
    }

    public Map<Integer, List<Instance>> getUtHash() {
        return utHash;
    }

    public void setUtHash(Map<Integer, List<Instance>> utHash) {
        this.utHash = utHash;
    }

    public Map<Integer, List<Instance>> getLtHash() {
        return ltHash;
    }

    public void setLtHash(Map<Integer, List<Instance>> ltHash) {
        this.ltHash = ltHash;
    }
}
