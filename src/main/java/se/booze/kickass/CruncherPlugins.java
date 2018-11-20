package se.booze.kickass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import kickass.plugins.interf.IPlugin;
import kickass.plugins.interf.archive.IArchive;

import se.triad.kickass.exomizer.RawExomizer;
import se.triad.kickass.exomizer.MemExomizer;
import se.triad.kickass.exomizer.LevelExomizer;
import se.triad.kickass.exomizer.ForwardMemExomizer;
import se.triad.kickass.AbstractCruncher;
import se.triad.kickass.AbstractSegmentCruncher;
import se.triad.kickass.exomizer.BackwardMemExomizer;
import se.booze.byteboozer.B2;
import se.booze.byteboozer.B2Exe;
import se.booze.byteboozer.ByteBoozer;
import se.booze.deltapacker.DeltaPacker;

public class CruncherPlugins implements IArchive {

	@Override
	public List<IPlugin> getPluginObjects() {

		List<IPlugin> list = new ArrayList<>();
		list.add(new RawExomizer());
		list.add(new MemExomizer());
		list.add(new LevelExomizer());
		list.add(new ForwardMemExomizer());
		list.add(new BackwardMemExomizer());
		
		list.add(new ByteBoozer());
		list.add(new B2());
		list.add(new B2Exe());

		list.addAll(
				list.stream()
					.filter(AbstractCruncher.class::isInstance)
					.map(AbstractCruncher.class::cast)
					.map(AbstractSegmentCruncher::new)
					.collect(Collectors.toList()));

		list.add(new DeltaPacker());

		return list;
	}

}

