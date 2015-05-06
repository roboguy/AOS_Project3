package edu.utdallas.aos.core;

public class OpContainer {
	private String content = "";
	private boolean quorumObtained = false;
	private String fileName = "";
	
	OpContainer(String fileName, boolean qObtained, String content){
		this.fileName = fileName;
		this.quorumObtained = qObtained;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public boolean isQuorumObtained() {
		return quorumObtained;
	}

	public String getFileName() {
		return fileName;
	}
	
}