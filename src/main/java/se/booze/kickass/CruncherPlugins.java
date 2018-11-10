package se.booze.kickass;

import java.util.ArrayList;
import java.util.List;

import kickass.plugins.interf.IArchive;

import se.triad.kickass.exomizer.RawExomizer;
import se.triad.kickass.exomizer.MemExomizer;
import se.triad.kickass.exomizer.LevelExomizer;
import se.triad.kickass.exomizer.ForwardMemExomizer;
import se.triad.kickass.exomizer.BackwardMemExomizer;
import se.booze.byteboozer.B2;
import se.booze.byteboozer.ByteBoozer;
import se.booze.deltapacker.DeltaPacker;

public class CruncherPlugins implements IArchive {

	@Override
	public List<Object> getPluginObjects() {

		List<Object> list = new ArrayList<Object>();
		list.add(new RawExomizer());
		list.add(new MemExomizer());
		list.add(new LevelExomizer());
		list.add(new ForwardMemExomizer());
		list.add(new BackwardMemExomizer());
		
		list.add(new ByteBoozer());
		list.add(new DeltaPacker());
		list.add(new B2());
	
		return list;
	}

}

