package mwdetection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
 * Reading the config.txt for programe control
 * Please find the readMe.txt for the meaning of different config number
 */
public class ReadConfig {
    private int _projectDataset;
    private String _projectName;
    private String _rootFolder;
    private String _outputFolder;
    private String _mlModel;

    public ReadConfig() throws IOException {
        String line=null;
        FileReader freader=new FileReader("config.txt");
        BufferedReader inputFile=new BufferedReader(freader);

        while ((line = inputFile.readLine())!= null) {
            if(line.equals("projectDataset:")){
                _projectDataset = Integer.parseInt(inputFile.readLine());
            }else if(line.equals("rootFolder:")){
                _rootFolder = inputFile.readLine();
            }else if(line.equals("outputFolder:")){
                _outputFolder = inputFile.readLine();
            }else if(line.equals("projectName:")){
                _projectName = inputFile.readLine();
            }else if(line.equals("MLModel:")){
                _mlModel = inputFile.readLine();
            }
        }

        inputFile.close();
    }

    public int get_projectDataset() {
        return _projectDataset;
    }

    public String get_mlModel(){
        return _mlModel;
    }

    public String get_projectName() {
        return _projectName;
    }

    public String get_rootFolder() {
        return _rootFolder;
    }

    public String get_outputFolder() {return _outputFolder; }
}
