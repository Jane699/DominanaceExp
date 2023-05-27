package featureSelection.tester.procedure.heuristic.dominanceApproximationCalculation;

import featureSelection.basic.lang.dataStructure.IntArrayKey;
import featureSelection.basic.model.universe.instance.Instance;
import featureSelection.basic.procedure.ProcedureComponent;
import featureSelection.basic.procedure.component.TimeCountedProcedureComponent;
import featureSelection.basic.procedure.container.DefaultProcedureContainer;
import featureSelection.basic.procedure.parameter.ProcedureParameters;
import featureSelection.basic.procedure.report.ReportMapGenerated;
import featureSelection.basic.procedure.statistics.Statistics;
import featureSelection.basic.procedure.statistics.StatisticsCalculated;
import featureSelection.basic.procedure.timer.TimeSum;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.Common.DominanceCommonCalculate;
import featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.DominanceEquivalenceClassBasedApproximationCalculationAlgorithm;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.ClassUnion;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.common.DominanceApproximation;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec.ClassUnionDecHash;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec.EquivalenceClass;
import featureSelection.repository.entity.alg.dominanceApproximationCalculation.dnec.PrepareResultPack;
import featureSelection.tester.procedure.ComponentTags;
import featureSelection.tester.procedure.param.ParameterConstants;
import featureSelection.tester.utils.ProcedureUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static featureSelection.repository.algorithm.alg.dominanceApproximationCalculation.DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.StringFormat.consDecFormat;


/**
 * @author daiYang_wu
 */
@Slf4j

public class DominanceEquivalenceClassBasedApproximationCalculationAlgorithmQRTester extends DefaultProcedureContainer<Collection<Integer>>
        implements TimeSum,
        ReportMapGenerated<String, Map<String, Object>>,
        StatisticsCalculated {

    private boolean logOn;
    @Getter
    private Statistics statistics;
    @Getter
    private Map<String, Map<String, Object>> report;

    public DominanceEquivalenceClassBasedApproximationCalculationAlgorithmQRTester(ProcedureParameters parameters, boolean logOn) {
        super(logOn ? log : null, parameters);
        this.logOn = logOn;
        statistics = new Statistics();
        report = new HashMap<>();
    }

    @Override
    public ProcedureComponent<?>[] initComponents() {
        return new ProcedureComponent<?>[]{

                new TimeCountedProcedureComponent<PrepareResultPack>(
                        ComponentTags.TAG_PREPARE,
                        this.getParameters(),
                        (component) -> {
                            if (logOn) {
                                log.info("1. " + component.getDescription());
                            }
                            component.setLocalParameters(new Object[]{
                                    getParameters().get(ParameterConstants.PARAMETER_UNIVERSE_INSTANCES),
                                    getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
                                    getParameters().get("decisionValues"),
                                    getParameters().get("strategy")

                            });
                        },
                        true,
                        (component, parameters) -> {
                            int p = 0;
                            Collection<Instance> dataset = (Collection<Instance>) parameters[p++];
                            int[] attributes = (int[]) parameters[p++];
                            List<Integer> decisionValues = (List<Integer>) parameters[p++];
                            String strategy = (String) parameters[p++];
                            Map<IntArrayKey, EquivalenceClass> decHash = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.dataReduction(dataset, attributes);
                            Collection<ClassUnion> classUnions = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.genClassUnions(decisionValues);
                            ClassUnionDecHash consDecHash = DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.calculateConsDec(decHash, classUnions, decisionValues, strategy);
                            PrepareResultPack prepareResultPack = new PrepareResultPack(decHash, consDecHash);
//                            if (logOn){
//                                log.info("\n" + prepareResultPack.getClassUnionDecHash().toString());
//                            }
                            return prepareResultPack;
                        }, (component, prepareResultPack) -> {
                    getParameters().set(true, "PrepareResultPack", prepareResultPack);
                    getStatistics().put("after_reduce_instance_num", prepareResultPack.getDecHash().size());
                }) {
                    @Override
                    public String staticsName() {
                        return shortName() + " | 1. of " + getComponents().size() + "." + " " + getDescription();
                    }

                    @Override
                    public void init() {

                    }
                }.setDescription("Prepare : genClassUnions , dataReduct , gen consDecHash"),
                new TimeCountedProcedureComponent<DominanceApproximation>(
                        ComponentTags.TAG_LOW_APPROXIMATION,
                        this.getParameters(),
                        (component) -> {
                            if (logOn) {
                                log.info("2. " + component.getDescription());
                            }
                            component.setLocalParameters(new Object[]{
                                    getParameters().get(ParameterConstants.PARAMETER_UNIVERSE_INSTANCES),
                                    getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
                                    getParameters().get("PrepareResultPack"),
                            });
                        },
                        true,
                        (component, parameters) -> {
                            int p = 0;
                            Collection<Instance> dataset = (Collection<Instance>) parameters[p++];
                            int[] attributes = (int[]) parameters[p++];
                            PrepareResultPack prepareResultPack = (PrepareResultPack) parameters[p++];
                            Map<ClassUnion, Collection<Instance>> lowApproximations = new LinkedHashMap<>();
                            Map<String, int[]> gNumMap = new HashMap<>();

                            long aGNum = 0;
                            long nGNum = 0;
                            long pGNum = 0;

                            for (ClassUnion classUnion : prepareResultPack.getClassUnionDecHash().getClassUnionList()) {
                                if (logOn) {
                                    log.info("  calculate lowApproximation for " + classUnion.toString());
//                                    log.info("aG num : " + prepareResultPack.getClassUnionDecHash().getClassUnionDecByClassUnion(classUnion).getPosDecList().size());
//                                    log.info("ag : {}   pg : {}  ng : {}"  ,
//                                            prepareResultPack.getClassUnionDecHash().getLowClassUnionDecPos(classUnion.getDecisionValue()).size(),
//                                            prepareResultPack.getClassUnionDecHash().getLowClassUnionDecBou(classUnion.getDecisionValue()).size(),
//                                            prepareResultPack.getClassUnionDecHash().getLowClassUnionDecNeg(classUnion.getDecisionValue()).size());
                                }
                                int[] gNum = new int[]{
                                        prepareResultPack.getClassUnionDecHash().getClassUnionDecByClassUnion(classUnion).getPosDecList().size(),
                                        prepareResultPack.getClassUnionDecHash().getClassUnionDecByClassUnion(classUnion).getNegDecList().size(),
                                        prepareResultPack.getClassUnionDecHash().getClassUnionDecByClassUnion(classUnion).getBouDecList().size()
                                }; //aG,nG,pG

                                aGNum += gNum[0];
                                nGNum += gNum[1];
                                pGNum += gNum[2];
                                String direction = classUnion.isBiggerOrEqual() ? ">=" : "<=";
                                gNumMap.put(direction + classUnion.getDecisionValue(), gNum);
                                lowApproximations.put(classUnion, DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.calculateLowApproximationByClassUnion(attributes, prepareResultPack.getClassUnionDecHash(), classUnion));
                            }
                            getStatistics().put("gNumMap", gNumMap);
                            getStatistics().put("aGNum", aGNum);
                            getStatistics().put("nGNum", nGNum);
                            getStatistics().put("pGNum", pGNum);
                            DominanceApproximation dominanceApproximation = new DominanceApproximation();
                            dominanceApproximation.setLowApproximations(lowApproximations);
                            return dominanceApproximation;
                        },
                        (component, dominanceApproximation) -> {
                            getParameters().set(true, "DominanceApproximation", dominanceApproximation);
                        }
                ) {
                    @Override
                    public void init() {

                    }

                    @Override
                    public String staticsName() {
                        return shortName() + " | 2. of " + getComponents().size() + "." + " " + getDescription();
                    }
                }.setDescription("calculate Low approximation"),
                new TimeCountedProcedureComponent<DominanceApproximation>(
                        ComponentTags.TAG_UPPER_APPROXIMATION,
                        this.getParameters(),
                        (component) -> {
                            if (logOn) {
                                log.info("3. " + component.getDescription());
                            }
                            component.setLocalParameters(new Object[]{
                                    getParameters().get(ParameterConstants.PARAMETER_UNIVERSE_INSTANCES),
                                    getParameters().get(ParameterConstants.PARAMETER_ATTRIBUTES),
                                    getParameters().get("PrepareResultPack"),
                                    getParameters().get("DominanceApproximation"),
                                    getParameters().get("strategy")

                            });
                        },
                        true,
                        (component, parameters) -> {
                            int p = 0;
                            Collection<Instance> dataset = (Collection<Instance>) parameters[p++];
                            int[] attributes = (int[]) parameters[p++];
                            PrepareResultPack prepareResultPack = (PrepareResultPack) parameters[p++];
                            DominanceApproximation dominanceApproximation = (DominanceApproximation) parameters[p++];
                            String strategy = (String) parameters[p++];
                            Map<ClassUnion, Collection<Instance>> upperApproximations = new LinkedHashMap<>();
                            if ("DIG".equals(strategy)) {
                                log.info("calculate upperApproximation using origin method");
                                for (ClassUnion classUnion : prepareResultPack.getClassUnionDecHash().getClassUnionList()) {
                                    if (logOn) log.info("  calculate upperApproximation for " + classUnion.toString());
                                    upperApproximations.put(classUnion, DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.calculateUpperApproximationByClassUnion(attributes, prepareResultPack.getClassUnionDecHash(), classUnion));
                                }
                            } else if ("DIG+Rule".equals(strategy)) {
                                log.info("calculate upperApproximation using rule method");
                                for (ClassUnion classUnion : prepareResultPack.getClassUnionDecHash().getClassUnionList()) {
                                    if (logOn) log.info("  calculate upperApproximation for " + classUnion.toString());
                                    upperApproximations.put(classUnion, DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.calculateUpperApproximationByClassUnionRule(attributes, prepareResultPack.getClassUnionDecHash(), classUnion));
                                }
                            }
                            // DIG+Rule+UpperSearch && DIG+UpperSearch
                            else {
                                log.info("calculate upperApproximation using upper search");
                                Map<Boolean, List<ClassUnion>> classUnionSortLink = new HashMap<>();
                                classUnionSortLink.put(true, new LinkedList<>());
                                classUnionSortLink.put(false, new LinkedList<>());
                                Map<ClassUnion, Map<IntArrayKey, EquivalenceClass>> preDIG = new HashMap<>();
                                for (ClassUnion classUnion : prepareResultPack.getClassUnionDecHash().getClassUnionList()) {
                                    preDIG.put(classUnion, new HashMap<>());
                                    if (classUnion.isBiggerOrEqual()) {
                                        classUnionSortLink.get(true).add(classUnion);
                                    } else {
                                        classUnionSortLink.get(false).add(classUnion);
                                    }
                                }


                                for (int i = 0; i < classUnionSortLink.get(false).size(); i++) {
                                    ClassUnion lowClassUnion = classUnionSortLink.get(false).get(i);
                                    if (logOn)
                                        log.info("  calculate upperApproximation for " + lowClassUnion.toString());

                                    upperApproximations.put(lowClassUnion,
                                            DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.calculateUpperApproximationByClassUnionRuleAccelerator(
                                                    attributes,
                                                    prepareResultPack.getClassUnionDecHash(),
                                                    lowClassUnion, classUnionSortLink.get(false),
                                                    i,
                                                    preDIG));
                                }
                                for (int i = classUnionSortLink.get(true).size() - 1; i >= 0; i--) {
                                    ClassUnion upperClassUnion = classUnionSortLink.get(true).get(i);
                                    if (logOn)
                                        log.info("  calculate upperApproximation for " + upperClassUnion.toString());
                                    upperApproximations.put(upperClassUnion,
                                            DominanceEquivalenceClassBasedApproximationCalculationAlgorithm.Basic.calculateUpperApproximationByClassUnionRuleAccelerator(
                                                    attributes,
                                                    prepareResultPack.getClassUnionDecHash(),
                                                    upperClassUnion, classUnionSortLink.get(false),
                                                    i,
                                                    preDIG));
                                }
                            }
                            dominanceApproximation.setUpperApproximations(upperApproximations);
                            return dominanceApproximation;
                        },
                        (component, dominanceApproximation) -> {
                            getParameters().set(true, "DominanceApproximation", dominanceApproximation);
                            getStatistics().put("compareCount", DominanceCommonCalculate.CompareCount.getCount());
                            DominanceCommonCalculate.CompareCount.reset();
                        }
                ) {
                    @Override
                    public void init() {

                    }

                    @Override
                    public String staticsName() {
                        return shortName() + " | 3. of " + getComponents().size() + "." + " " + getDescription();
                    }
                }.setDescription("calculate Upper approximation"),
        };
    }

    @Override
    public String shortName() {
        return "calculate lowerApproximation by DNEC";
    }

    @Override
    public String reportName() {
        return shortName();
    }


    @Override
    public String staticsName() {
        return shortName();
    }

    @Override
    public long getTime() {
        return getComponents().stream()
                .map(comp -> ProcedureUtils.Time.sumProcedureComponentTimes(comp))
                .reduce(Long::sum).orElse(0L);
    }

    @Override
    public Map<String, Long> getTimeDetailByTags() {
        return ProcedureUtils.Time.sumProcedureComponentsTimesByTags(this);
    }

    @Override
    public String[] getReportMapKeyOrder() {
        return getComponents().stream().map(ProcedureComponent::getDescription).toArray(String[]::new);
    }
}
