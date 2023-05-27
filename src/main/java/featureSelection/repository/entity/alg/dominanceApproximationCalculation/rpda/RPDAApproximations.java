package featureSelection.repository.entity.alg.dominanceApproximationCalculation.rpda;

import featureSelection.basic.model.universe.instance.Instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author daiYang_wu
 */
public class RPDAApproximations {
    Map<Integer, List<Instance>> PUL;
    Map<Integer, List<Instance>> PUU;
    Map<Integer, List<Instance>> PLU;
    Map<Integer, List<Instance>> PLL;

    public RPDAApproximations(int size) {
        PUL = new HashMap<>(size);
        PUU = new HashMap<>(size);
        PLU = new HashMap<>(size);
        PLL = new HashMap<>(size);
    }

    public void initRPDAApproximations(List<Integer> decisionValues) {
        for (int index = 0; index < decisionValues.size(); index++) {
            Integer d = decisionValues.get(index);
            PUU.put(d, new ArrayList<>());
            PLU.put(d, new ArrayList<>());
            PUL.put(d, new ArrayList<>());
            PLL.put(d, new ArrayList<>());
        }
    }

    public Map<Integer, List<Instance>> getPUL() {
        return PUL;
    }

    public Map<Integer, List<Instance>> getPUU() {
        return PUU;
    }

    public Map<Integer, List<Instance>> getPLU() {
        return PLU;
    }

    public Map<Integer, List<Instance>> getPLL() {
        return PLL;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Integer> ds = this.PLL.keySet().stream().sorted().collect(Collectors.toList());
        for (int index = 0; index < ds.size(); index++) {
            Integer t = ds.get(index);
            if (index != 0) {
                sb.append("P-Upper(>=" + t + "):" + this.PUU.get(t).stream().map(instance -> instance.getNum()).distinct().sorted().collect(Collectors.toList()) + "\n");
                sb.append("P-Low(>=" + t + "):" + this.PLU.get(t).stream().map(instance -> instance.getNum()).distinct().sorted().collect(Collectors.toList()) + "\n");
            }
            if (index != ds.size() - 1) {
                sb.append("P-Upper( <=" + t + "):" + this.PUL.get(t).stream().map(instance -> instance.getNum()).distinct().sorted().collect(Collectors.toList()) + "\n");
                sb.append("P-Low(<=" + t + "):" + this.PLL.get(t).stream().map(instance -> instance.getNum()).distinct().sorted().collect(Collectors.toList()) + "\n");
            }
        }
        return sb.toString();
    }
}
