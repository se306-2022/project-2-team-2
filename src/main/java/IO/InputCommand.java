package IO;

public class InputCommand {
    private String inputFile;
    private String outputFile;

    private boolean customOutputFile = false;

    public InputCommand() {
    }

    public InputCommand(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getInputFile() {
        return this.inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFile() {
        //If no output file is specified, use an output file name of {input}-output.dot
        if (this.outputFile == null) {
            this.outputFile = inputFile.replace(".dot", "") + "-output.dot";
        }
        return this.outputFile;
    }
    public void setOutputFile(String outputFile) {
        if (!outputFile.endsWith(".dot")) {
            outputFile += ".dot";
        }
        this.outputFile = outputFile;
    }

    public boolean isCustomOutputFile() {
        return this.customOutputFile;
    }

    public void setCustomOutputFile(boolean isCustom) {
        this.customOutputFile = isCustom;
    }


}