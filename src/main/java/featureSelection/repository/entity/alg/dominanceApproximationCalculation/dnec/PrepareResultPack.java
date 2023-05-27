package featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec;

import featureSelection.basic.lang.dataStructure.IntArrayKey;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @author daiYang_wu
 */
@Data
@AllArgsConstructor
public class PrepareResultPack {
    private Map<IntArrayKey, EquivalenceClass> decHash;
    private ClassUnionDecHash classUnionDecHash;

}
