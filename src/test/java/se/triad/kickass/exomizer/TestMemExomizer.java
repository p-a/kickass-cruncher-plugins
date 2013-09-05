package se.triad.kickass.exomizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import cml.kickass.plugins.interf.IEngine;
import cml.kickass.plugins.interf.IMemoryBlock;
import cml.kickass.plugins.interf.IValue;

@Test
public class TestMemExomizer {
	
	public static class StubEngine implements IEngine {

		@Override
		public void error(String arg0) {
			throw new RuntimeException(arg0);
		}

		@Override
		public File getCurrentDirectory() {
			return null;
		}

		@Override
		public File getFile(String arg0) {
			return null;
		}

		@Override
		public void print(String arg0) {
			System.out.println(arg0);
		}

		@Override
		public void printNow(String arg0) {
			System.out.println(arg0);
		}
		
	}

	@Test
	public void testLiteralsBackward() throws Exception {
		
		byte[] in = resourceToByteArray("./elgena.png");
		
		MemExomizer memExomizer = new MemExomizer();
		
		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		
		byte[] result = memExomizer.execute(blocks, new IValue[]{}, new StubEngine());
		
		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_literals.exo");
		
		Assert.assertEquals(result, expected);
		
	}

	@Test
	public void testBlobLiteralsBackward() throws Exception {
		
		byte[] in = resourceToByteArray("./elgena.png");
		
		MemExomizer memExomizer = new MemExomizer();
		
		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		blocks.add(new MemBlock("elgena", in, 0x8000));
		
		byte[] result = memExomizer.execute(blocks, new IValue[]{}, new StubEngine());
		
		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_0x8000.exo");
		
		Assert.assertEquals(result, expected);
		
	}
	
	@Test
	public void testBlobLiteralsForward() throws Exception {
		
		byte[] in = resourceToByteArray("./elgena.png");
		
		MemExomizer memExomizer = new MemExomizer();
		
		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		blocks.add(new MemBlock("elgena", in, 0x8000));
		
		byte[] result = memExomizer.execute(blocks, new IValue[]{new BooleanValue(true)}, new StubEngine());
		
		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_0x8000_forward.exo");
		
		Assert.assertEquals(result, expected);
		
	}

	@Test
	public void testLiteralsForward() throws Exception {
		
		byte[] in = resourceToByteArray("./elgena.png");
		
		MemExomizer memExomizer = new MemExomizer();
		
		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		
		byte[] result = memExomizer.execute(blocks, new IValue[]{new BooleanValue(true)}, new StubEngine());
		
		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_literals_forward.exo");
		
		Assert.assertEquals(result, expected);
		
	}

	@Test
	public void testNoLiteralsForward() throws Exception {
		
		byte[] in = resourceToByteArray("./elgena.png");
		
		MemExomizer memExomizer = new MemExomizer();
		
		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		
		byte[] result = memExomizer.execute(blocks, new IValue[]{new BooleanValue(true), new BooleanValue(false)}, new StubEngine());
		
		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_noliterals_forward.exo");
		
		Assert.assertEquals(result, expected);
		
	}

	@Test
	public void testNoLiteralsBackward() throws Exception {
		
		byte[] in = resourceToByteArray("./elgena.png");
		
		MemExomizer memExomizer = new MemExomizer();
		
		List<IMemoryBlock> blocks = new ArrayList<IMemoryBlock>();
		blocks.add(new MemBlock("elgena", in, 0x2000));
		
		byte[] result = memExomizer.execute(blocks, new IValue[]{new BooleanValue(false), new BooleanValue(false)}, new StubEngine());
		
		byte[] expected = resourceToByteArray("./elgena_mem_0x2000_noliterals.exo");
		
		Assert.assertEquals(result, expected);
		
	}

	protected byte[] resourceToByteArray(String resource) throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
		byte [] buf = new byte[8192];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int read;
		while ( (read = is.read(buf)) > 0){
			baos.write(buf,0,read);
		}
		is.close();
		return baos.toByteArray();
	}
}
