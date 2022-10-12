package IO;

public class InputCommand {
    private String inputFile;
    private String outputFile;
    private int numProcessors;
    private boolean isParallel = false;
    private int numParallelCores;
    private boolean isVisual = false;
    private boolean customOutputFile = false;

    public InputCommand() {
    }

    public InputCommand(String inputFile, int numProcessors) {
        this.inputFile = inputFile;
        this.numProcessors = numProcessors;
    }

    public void setNumProcessors(int numProcessors) {
        this.numProcessors = numProcessors;
    }

    public int getNumProcessors() { return this.numProcessors; }

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

    public boolean isParallel() {
        return this.isParallel;
    }
    public void setParallel(boolean parallel) {
        isParallel = parallel;
    }

    public boolean isVisual() {
        return this.isVisual;
    }
    public void setVisual(boolean visual) {
        isVisual = visual;
    }

    public void setNumParallelCores(int numParallelCores) {
        this.numParallelCores = numParallelCores;
    }

    public int getNumParallelCores() throws IllegalAccessException {
        if (!this.isParallel) {
            throw new IllegalAccessException(
                    "Program is NOT specified to run in parallel");
        }
        return this.numParallelCores;
    }
    public boolean isCustomOutputFile() {
        return this.customOutputFile;
    }

    public void setCustomOutputFile(boolean isCustom) {
        this.customOutputFile = isCustom;
    }

}
