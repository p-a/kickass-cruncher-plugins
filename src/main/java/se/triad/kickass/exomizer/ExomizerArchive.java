package se.triad.kickass.exomizer;

import java.util.ArrayList;
import java.util.List;

import cml.kickass.plugins.interf.IArchive;

public class ExomizerArchive implements IArchive {

	@Override
	public List<Object> getPluginObjects() {

		List<Object> list = new ArrayList<Object>();
		list.add(new RawExomizer());
		list.add(new MemExomizer());
		list.add(new LevelExomizer());
		list.add(new ForwardMemExomizer());
		list.add(new BackwardMemExomizer());
	
		return list;
	}

}
