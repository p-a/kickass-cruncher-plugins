package se.triad.kickass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import kickass.nonasm.tools.tuples.Pair;
import kickass.plugins.interf.general.IEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IParameterMap;
import kickass.plugins.interf.segmentmodifier.ISegmentModifier;
import kickass.plugins.interf.segmentmodifier.SegmentModifierDefinition;
import se.triad.kickass.AbstractCruncher.CruncherContext;

public class AbstractSegmentCruncher implements ISegmentModifier {

	private AbstractCruncher abstractCruncher;
	private SegmentModifierDefinition segmentModifierDefinition = new SegmentModifierDefinition();
	
	public AbstractSegmentCruncher(AbstractCruncher abstractCruncher) {
		this.abstractCruncher = abstractCruncher;
		this.segmentModifierDefinition.setName(abstractCruncher.getName());
	}

	private Function<CruncherContext, List<IMemoryBlock>> postProcessor = context -> {
		List<Pair<IMemoryBlock, CrunchedObject>> tuples = new ArrayList<>();
		for (int i = 0; i < context.blocks.size(); i++) {
			tuples.add(new Pair<>(context.blocks.get(i), context.crunchedObjects.get(i)));
		}
		return tuples.stream().map(
				tuple -> context.engine.createMemoryBlock(
						tuple.getA().getName(),
						tuple.getB().address,
						tuple.getB().data))
				.collect(Collectors.toList());
	};
	
	@Override
	public List<IMemoryBlock> execute(List<IMemoryBlock> blocks, IParameterMap parameters, IEngine engine) {
		return abstractCruncher.execute(blocks, parameters, engine, postProcessor);
	}

	@Override
	public SegmentModifierDefinition getDefinition() {
		return segmentModifierDefinition;
	}

}
