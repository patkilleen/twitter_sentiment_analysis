package io;

import java.nio.file.Path;

public class FoldInfo {

	private Path originalFile;
	private Path originalFileDelta;
	private Path foldFile;
	
	private int startIx;
	private int endIx;
	private int originalFileNumLines;
	public FoldInfo(Path originalFile, Path originalFileDelta, Path foldFile, int startIx, int endIx,
			int originalFileNumLines) {
		super();
		this.originalFile = originalFile;
		this.originalFileDelta = originalFileDelta;
		this.foldFile = foldFile;
		this.startIx = startIx;
		this.endIx = endIx;
		this.originalFileNumLines = originalFileNumLines;
	}
	public Path getOriginalFile() {
		return originalFile;
	}
	public void setOriginalFile(Path originalFile) {
		this.originalFile = originalFile;
	}
	public Path getOriginalFileDelta() {
		return originalFileDelta;
	}
	public void setOriginalFileDelta(Path originalFileDelta) {
		this.originalFileDelta = originalFileDelta;
	}
	public Path getFoldFile() {
		return foldFile;
	}
	public void setFoldFile(Path foldFile) {
		this.foldFile = foldFile;
	}
	public int getStartIx() {
		return startIx;
	}
	public void setStartIx(int startIx) {
		this.startIx = startIx;
	}
	public int getEndIx() {
		return endIx;
	}
	public void setEndIx(int endIx) {
		this.endIx = endIx;
	}
	public int getOriginalFileNumLines() {
		return originalFileNumLines;
	}
	public void setOriginalFileNumLines(int originalFileNumLines) {
		this.originalFileNumLines = originalFileNumLines;
	}
	


	
	
}
