package featureSelection.tester.procedure.heuristic.dominanceApproximationCalculation;

import com.alibaba.fastjson.JSONObject;
import common.utils.ArrayUtils;
import featureSelection.basic.lang.dataStructure.IntArrayKey;
import featureSelection.basic.model.universe.instance.Instance;
import featureSelection.basic.procedure.parameter.ProcedureParameters;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.ClassicApproximationCalculationAlgorithm;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.Common.DominanceCommonCalculate;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.DominanceEquivalenceClassBasedApproximationCalculationAlgorithm;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.ParallelApproachCalculateApproximationsAlgorithm;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.DominanceApproximation;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec.ClassUnionDecHash;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec.EquivalenceClass;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.opac.OPACApproximations;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.rpda.RPDAApproximations;
import featureSelection.tester.procedure.basic.BasicTester;
import featureSelection.tester.procedure.param.ParameterConstants;
import featureSelection.tester.utils.DBUtils;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.Common.DominanceCommonCalculate.judgeIsDominance;

/**
 * @author daiYang_wu
 */
public class BasicAlgTest extends BasicTester {
    //    @Test
    public void testCalculateLowApproximationClassic() throws Exception {
        List<Integer> decisionValues = (List<Integer>) DBUtils.DatasetDecisionValues.get(instances);
        Collections.sort(decisionValues);
        ProcedureParameters parameters =
                new ProcedureParameters()
                        // U
                        .set(true, ParameterConstants.PARAMETER_UNIVERSE_INSTANCES, instances)
                        // C
                        .set(true, ParameterConstants.PARAMETER_ATTRIBUTES, getAllConditionalAttributes())
                        .set(true, "decisionValues", decisionValues);
        ClassicCalculateApproximationsAlgorithmQRTester classicCalculateApproximationsAlgorithmQRTester = new ClassicCalculateApproximationsAlgorithmQRTester(parameters, true);
        DominanceApproximation results = (DominanceApproximation) classicCalculateApproximationsAlgorithmQRTester.exec();

        Map<ClassUnion, Collection<Instance>> classUnionMap = DominanceCommonCalculate.calculateClassUnionSet(instances, decisionValues);
        for (ClassUnion classUnion : classUnionMap.keySet()) {

            System.out.println(
                    classUnion.toString() + "-------->\n"
                            + "upper: " + results.getUpperApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString() + "\n"
                            + "lower: " + results.getLowApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString());

        }
        System.out.println("times : " + classicCalculateApproximationsAlgorithmQRTester.getTime() / 1000.0 / 1000.0 + "ms");

    }

    @Test
    public void testCalculateLowApproximationPIAC() throws Exception {
        List<Integer> decisionValues = (List<Integer>) DBUtils.DatasetDecisionValues.get(instances);
        Collections.sort(decisionValues);
        Map<ClassUnion, Collection<Instance>> classUnionMap = DominanceCommonCalculate.calculateClassUnionSet(instances, decisionValues);
        ProcedureParameters parameters =
                new ProcedureParameters()
                        // U
                        .set(true, ParameterConstants.PARAMETER_UNIVERSE_INSTANCES, instances)
                        // C
                        .set(true, ParameterConstants.PARAMETER_ATTRIBUTES, getAllConditionalAttributes())
                        .set(true, "decisionValues", decisionValues);

        ParallelApproachCalculateApproximationsAlgorithmHeuristicQRTester tester1 =
                new ParallelApproachCalculateApproximationsAlgorithmHeuristicQRTester(parameters, true);

        DominanceApproximation results = (DominanceApproximation) tester1.exec();
        for (ClassUnion classUnion : classUnionMap.keySet()) {

            System.out.println(
                    classUnion.toString() + "-------->\n"
                            + "upper: " + results.getUpperApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString() + "\n"
                            + "lower: " + results.getLowApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString());

        }
        tester1.getTimeDetailByTags().entrySet().stream().forEach(stringLongEntry -> {
            System.out.println(stringLongEntry.getKey().toString() + " : " + stringLongEntry.getValue() / 1000.0 / 1000.0);
        });
        System.out.println("compareCount:" + tester1.getStatistics().get("compareCount"));

    }

    @Test
    public void testCalculateLowApproximationDREC() throws Exception {
        //prepare parm
        List<Integer> decisionValues = (List<Integer>) DBUtils.DatasetDecisionValues.get(instances);
        Collections.sort(decisionValues);
        // init decHash
        Map<IntArrayKey, EquivalenceClass> decHash = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.dataReduction(instances, getAllConditionalAttributes());
//        System.out.println(DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.StringFormat.decHashFormat(decHash));
        //init classUnion
        Collection<ClassUnion> classUnions = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.genClassUnions(decisionValues);
        //init ConsDecHash
        ClassUnionDecHash consDecHash = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.calculateConsDec(decHash, classUnions, decisionValues, "DIG+RUle");
//        System.out.println(DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.StringFormat.consDecFormat(consDecHash));
        ProcedureParameters parametersDNEC =
                new ProcedureParameters()
                        // U
                        .set(true, ParameterConstants.PARAMETER_UNIVERSE_INSTANCES, instances)
                        // C
                        .set(true, ParameterConstants.PARAMETER_ATTRIBUTES, getAllConditionalAttributes())
                        .set(true, "decisionValues", decisionValues)
                        .set(true, "strategy", "DIG");


        DominanceEquivalenceClassBasedApproximationCalculationAlgorithmQRTester testerDNEC =
                new DominanceEquivalenceClassBasedApproximationCalculationAlgorithmQRTester(parametersDNEC, true);
        DominanceApproximation results = (DominanceApproximation) testerDNEC.exec();
//        for (ClassUnion classUnion : consDecHash.getClassUnionList()) {
//
//            System.out.println(
//                    classUnion.toString() + "-------->\n"
//                            + "upper: " + results.getUpperApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString() + "\n"
//                            + "lower: " + results.getLowApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString());
//
//        }
        testerDNEC.getTimeDetailByTags().entrySet().stream().forEach(stringLongEntry -> {
            System.out.println(stringLongEntry.getKey().toString() + " : " + stringLongEntry.getValue() / 1000.0 / 1000.0);
        });
        System.out.println("compareCount:" + testerDNEC.getStatistics().get("compareCount"));
        System.out.println("aGNum:" + testerDNEC.getStatistics().get("aGNum"));
        System.out.println("gNumMap:" +new JSONObject(testerDNEC.getStatistics().get("gNumMap")));

    }

    @Test
    public void testCalculateLowApproximationDRECRule() throws Exception {
        //prepare parm
        List<Integer> decisionValues = (List<Integer>) DBUtils.DatasetDecisionValues.get(instances);
        Collections.sort(decisionValues);
        // init decHash
        Map<IntArrayKey, EquivalenceClass> decHash = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.dataReduction(instances, getAllConditionalAttributes());
//        System.out.println(DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.StringFormat.decHashFormat(decHash));
        //init classUnion
        Collection<ClassUnion> classUnions = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.genClassUnions(decisionValues);
        //init ConsDecHash
        ClassUnionDecHash consDecHash = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.calculateConsDec(decHash, classUnions, decisionValues, "DIG+RUle");
//        System.out.println(DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.StringFormat.consDecFormat(consDecHash));
        ProcedureParameters parametersDNEC =
                new ProcedureParameters()
                        // U
                        .set(true, ParameterConstants.PARAMETER_UNIVERSE_INSTANCES, instances)
                        // C
                        .set(true, ParameterConstants.PARAMETER_ATTRIBUTES, getAllConditionalAttributes())
                        .set(true, "decisionValues", decisionValues)
                        .set(true, "strategy", "DIG+Rule");


        DominanceEquivalenceClassBasedApproximationCalculationAlgorithmQRTester testerDNEC =
                new DominanceEquivalenceClassBasedApproximationCalculationAlgorithmQRTester(parametersDNEC, true);
        DominanceApproximation results = (DominanceApproximation) testerDNEC.exec();
//        for (ClassUnion classUnion : consDecHash.getClassUnionList()) {
//
//            System.out.println(
//                    classUnion.toString() + "-------->\n"
//                            + "upper: " + results.getUpperApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString() + "\n"
//                            + "lower: " + results.getLowApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString());
//
//        }
        testerDNEC.getTimeDetailByTags().entrySet().stream().forEach(stringLongEntry -> {
            System.out.println(stringLongEntry.getKey().toString() + " : " + stringLongEntry.getValue() / 1000.0 / 1000.0);
        });
        System.out.println("compareCount:" + testerDNEC.getStatistics().get("compareCount"));
        System.out.println("aGNum:" + testerDNEC.getStatistics().get("aGNum"));
        System.out.println("gNumMap:" +new JSONObject(testerDNEC.getStatistics().get("gNumMap")));

    }

    @Test
    public void testCalculateLowApproximationDRECUpperSearch() throws Exception {
        //prepare parm
        List<Integer> decisionValues = (List<Integer>) DBUtils.DatasetDecisionValues.get(instances);
        Collections.sort(decisionValues);
        // init decHash
        Map<IntArrayKey, EquivalenceClass> decHash = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.dataReduction(instances, getAllConditionalAttributes());
//        System.out.println(DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.StringFormat.decHashFormat(decHash));
        //init classUnion
        Collection<ClassUnion> classUnions = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.genClassUnions(decisionValues);
        //init ConsDecHash
        ClassUnionDecHash consDecHash = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.calculateConsDec(decHash, classUnions, decisionValues, "DIG+RUle");
//        System.out.println(DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.StringFormat.consDecFormat(consDecHash));
        ProcedureParameters parametersDNEC =
                new ProcedureParameters()
                        // U
                        .set(true, ParameterConstants.PARAMETER_UNIVERSE_INSTANCES, instances)
                        // C
                        .set(true, ParameterConstants.PARAMETER_ATTRIBUTES, getAllConditionalAttributes())
                        .set(true, "decisionValues", decisionValues)
                        .set(true, "strategy", "DIG+UpperSearch");


        DominanceEquivalenceClassBasedApproximationCalculationAlgorithmQRTester testerDNEC =
                new DominanceEquivalenceClassBasedApproximationCalculationAlgorithmQRTester(parametersDNEC, true);
        DominanceApproximation results = (DominanceApproximation) testerDNEC.exec();
//        for (ClassUnion classUnion : consDecHash.getClassUnionList()) {
//
//            System.out.println(
//                    classUnion.toString() + "-------->\n"
//                            + "upper: " + results.getUpperApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString() + "\n"
//                            + "lower: " + results.getLowApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString());
//
//        }
        testerDNEC.getTimeDetailByTags().entrySet().stream().forEach(stringLongEntry -> {
            System.out.println(stringLongEntry.getKey().toString() + " : " + stringLongEntry.getValue() / 1000.0 / 1000.0);
        });
        System.out.println("compareCount:" + testerDNEC.getStatistics().get("compareCount"));
        System.out.println("aGNum:" + testerDNEC.getStatistics().get("aGNum"));
        System.out.println("gNumMap:" +new JSONObject(testerDNEC.getStatistics().get("gNumMap")));

    }

    @Test
    public void testCalculateLowApproximationDRECRuleUpperSearch() throws Exception {
        //prepare parm
        List<Integer> decisionValues = (List<Integer>) DBUtils.DatasetDecisionValues.get(instances);
        Collections.sort(decisionValues);
        // init decHash
        Map<IntArrayKey, EquivalenceClass> decHash = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.dataReduction(instances, getAllConditionalAttributes());
//        System.out.println(DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.StringFormat.decHashFormat(decHash));
        //init classUnion
        Collection<ClassUnion> classUnions = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.genClassUnions(decisionValues);
        //init ConsDecHash
        ClassUnionDecHash consDecHash = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.calculateConsDec(decHash, classUnions, decisionValues, "DIG+RUle");
//        System.out.println(DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.StringFormat.consDecFormat(consDecHash));
        ProcedureParameters parametersDNEC =
                new ProcedureParameters()
                        // U
                        .set(true, ParameterConstants.PARAMETER_UNIVERSE_INSTANCES, instances)
                        // C
                        .set(true, ParameterConstants.PARAMETER_ATTRIBUTES, getAllConditionalAttributes())
                        .set(true, "decisionValues", decisionValues)
                        .set(true, "strategy", "DIG+Rule+UpperSearch");


        DominanceEquivalenceClassBasedApproximationCalculationAlgorithmQRTester testerDNEC =
                new DominanceEquivalenceClassBasedApproximationCalculationAlgorithmQRTester(parametersDNEC, true);
        DominanceApproximation results = (DominanceApproximation) testerDNEC.exec();
//        for (ClassUnion classUnion : consDecHash.getClassUnionList()) {
//
//            System.out.println(
//                    classUnion.toString() + "-------->\n"
//                            + "upper: " + results.getUpperApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString() + "\n"
//                            + "lower: " + results.getLowApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString());
//
//        }
        testerDNEC.getTimeDetailByTags().entrySet().stream().forEach(stringLongEntry -> {
            System.out.println(stringLongEntry.getKey().toString() + " : " + stringLongEntry.getValue() / 1000.0 / 1000.0);
        });
        System.out.println("compareCount:" + testerDNEC.getStatistics().get("compareCount"));
        System.out.println("aGNum:" + testerDNEC.getStatistics().get("aGNum"));
        System.out.println("gNumMap:" +new JSONObject(testerDNEC.getStatistics().get("gNumMap")));

    }

    //    @Test
    public void testCalculateLowApproximationRPDA() throws Exception {
        List<Integer> decisionValues = (List<Integer>) DBUtils.DatasetDecisionValues.get(instances);
        Collections.sort(decisionValues);
        ProcedureParameters parameters =
                new ProcedureParameters()
                        // U
                        .set(true, ParameterConstants.PARAMETER_UNIVERSE_INSTANCES, instances)
                        // C
                        .set(true, ParameterConstants.PARAMETER_ATTRIBUTES, getAllConditionalAttributes())
                        .set(true, "decisionValues", decisionValues);
        RPDACalculateApproximationsAlgorithmQRTester rpdaCalculateApproximationsAlgorithmQRTester = new RPDACalculateApproximationsAlgorithmQRTester(parameters, true);
        RPDAApproximations rpdaApproximations = (RPDAApproximations) rpdaCalculateApproximationsAlgorithmQRTester.exec();
        System.out.println(rpdaApproximations.toString());
        System.out.println("times : " + rpdaCalculateApproximationsAlgorithmQRTester.getTime() / 1000.0 / 1000.0 + "ms");
    }

    //    @Test
    public void testCalculateLowApproximationOPAC() throws Exception {

        List<Integer> decisionValues = (List<Integer>) DBUtils.DatasetDecisionValues.get(instances);
        Collections.sort(decisionValues);
        ProcedureParameters parameters =
                new ProcedureParameters()
                        // U
                        .set(true, ParameterConstants.PARAMETER_UNIVERSE_INSTANCES, instances)
                        // C
                        .set(true, ParameterConstants.PARAMETER_ATTRIBUTES, getAllConditionalAttributes())
                        // D
                        .set(true, "decisionValues", decisionValues);

        OptimizedMethodCalculateApproximationsAlgorithmQRTester tester1 =
                new OptimizedMethodCalculateApproximationsAlgorithmQRTester(parameters, true);
        OPACApproximations approximations = (OPACApproximations) tester1.exec();
        System.out.println("times : " + tester1.getTime() / 1000.0 / 1000.0 + "ms");
    }

    @Test
    public void test() throws Exception {
        //prepare parm
        List<Integer> decisionValues = (List<Integer>) DBUtils.DatasetDecisionValues.get(instances);
        Collections.sort(decisionValues);
        Map<ClassUnion, Collection<Instance>> classUnionMap = DominanceCommonCalculate.calculateClassUnionSet(instances, decisionValues);
        ProcedureParameters parameters =
                new ProcedureParameters()
                        // U
                        .set(true, ParameterConstants.PARAMETER_UNIVERSE_INSTANCES, instances)
                        // C
                        .set(true, ParameterConstants.PARAMETER_ATTRIBUTES, getAllConditionalAttributes())
                        .set(true, "decisionValues", decisionValues);

        UseStrategiesParallelApproachCalculateApproximationsAlgorithmHeuristicQRTester tester1 =
                new UseStrategiesParallelApproachCalculateApproximationsAlgorithmHeuristicQRTester(parameters, true);

        DominanceApproximation results = (DominanceApproximation) tester1.exec();
        for (ClassUnion classUnion : classUnionMap.keySet()) {

            System.out.println(
                    classUnion.toString() + "-------->\n"
//                            + "upper: " + results.getUpperApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString() + "\n"
                            + "lower: " + results.getLowApproximations().get(classUnion).stream().map(ins -> ins.getNum()).sorted().collect(Collectors.toList()).toString());

        }
        tester1.getTimeDetailByTags().entrySet().stream().forEach(stringLongEntry -> {
            System.out.println(stringLongEntry.getKey().toString() + " : " + stringLongEntry.getValue() / 1000.0 / 1000.0);
        });
        System.out.println("compareCount:" + tester1.getStatistics().get("compareCount"));

    }

    @Test
    public void test2() throws Exception {
        //prepare parm
        List<Integer> decisionValues = (List<Integer>) DBUtils.DatasetDecisionValues.get(instances);
        Collections.sort(decisionValues);
        Map<ClassUnion, Collection<Instance>> classUnionMap = DominanceCommonCalculate.calculateClassUnionSet(instances, decisionValues);
        for (int i = 0 ; i < decisionValues.size()-1 ; i++){
            System.out.println(classUnionMap.get(new ClassUnion(decisionValues.get(i),false)).size());
        }

        System.out.println("=================");

        Map<ClassUnion, Collection<Instance>> DSLACClassUnionMap = ParallelApproachCalculateApproximationsAlgorithm.Basic.classUnionByDSLAC(decisionValues,classUnionMap);
        for (int i = 0 ; i < decisionValues.size()-1 ; i++){
            System.out.println(DSLACClassUnionMap.get(new ClassUnion(decisionValues.get(i),false)).size());
        }

        System.out.println("ok");
    }


}