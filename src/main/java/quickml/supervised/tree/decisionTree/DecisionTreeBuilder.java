
package quickml.supervised.tree.decisionTree;

import com.google.common.collect.Maps;
import quickml.data.ClassifierInstance;
import quickml.data.PredictionMap;
import quickml.supervised.tree.TreeBuilder;
import quickml.supervised.tree.branchSplitStatistics.InstancesToAttributeStatistics;
import quickml.supervised.tree.configurations.InitializedTreeConfig;
import quickml.supervised.tree.configurations.TreeConfig;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.DTNode;
import quickml.supervised.tree.nodes.Node;

import java.util.Map;

/**
 * Created by alexanderhawk on 4/20/15.
 */
public class DecisionTreeBuilder<I extends ClassifierInstance> extends TreeBuilder<Object, PredictionMap, I, ClassificationCounter, DecisionTree, ClassifierDataProperties> {

    @Override
    protected Map<BranchType, InstancesToAttributeStatistics<Object, I, ClassificationCounter>> initializeInstancesToAttributeStatistics(InitializedTreeConfig<ClassificationCounter, ClassifierDataProperties> initializedTreeConfig) {
        //In general the instancesToAttributeStats may depend on training data, hence we put them here to have access to the the initializad configurations.

        //alternative: each branchFinder get's initialized by a method that takes the training data

        Map<BranchType, InstancesToAttributeStatistics<Object, I, ClassificationCounter>> instancesToAttributeStatisticsMap = Maps.newHashMap();
        if (initializedTreeConfig.getDataProperties() instanceof  BinaryClassifierDataProperties) {
            instancesToAttributeStatisticsMap.put(BranchType.CATEGORICAL, new InstanceToAttributeStatisticsForBinaryClassCatBranch<I>(((BinaryClassifierDataProperties) initializedTreeConfig.getDataProperties()).minorityClassification));
        } else {
            instancesToAttributeStatisticsMap.put(BranchType.CATEGORICAL, new InstanceToAttributeStatisticsForCatBranch<I>());
        }
        instancesToAttributeStatisticsMap.put(BranchType.NUMERIC, new InstanceToAttributeStatisticsNumericBranch<I>());
        instancesToAttributeStatisticsMap.put(BranchType.BOOLEAN, new InstanceToAttributeStatisticsForCatBranch<I>());

        return  instancesToAttributeStatisticsMap;
    }


    public DecisionTreeBuilder(TreeConfig<ClassificationCounter, ClassifierDataProperties> treeConfig) {
        super(treeConfig, new DecisionTreeConfigInitializer(), new AggregateClassificationCounts<I>());
    }

    @Override
    public TreeBuilder<Object, PredictionMap, I, ClassificationCounter, DecisionTree, ClassifierDataProperties> copy() {
        return new DecisionTreeBuilder<>(treeConfig);
    }


    //TODO need better solution than casting
    @Override
    protected DecisionTree constructTree(Node<ClassificationCounter> node, ClassifierDataProperties dataProperties) {
        return new DecisionTree((DTNode)node, dataProperties.getClassifications());
    }

 }
