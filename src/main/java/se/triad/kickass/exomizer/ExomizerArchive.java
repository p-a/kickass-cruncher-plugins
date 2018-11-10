package se.triad.kickass.exomizer;

import java.util.ArrayList;
import java.util.List;

import kickass.plugins.interf.IPlugin;
import kickass.plugins.interf.archive.IArchive;

public class ExomizerArchive implements IArchive {

	@Override
	public List<IPlugin> getPluginObjects() {

		List<IPlugin> list = new ArrayList<>();
		list.add(new RawExomizer());
		list.add(new MemExomizer());
		list.add(new LevelExomizer());
		list.add(new ForwardMemExomizer());
		list.add(new BackwardMemExomizer());
	
		return list;
	}

}
