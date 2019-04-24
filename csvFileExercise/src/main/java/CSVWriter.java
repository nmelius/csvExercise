import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.*;

public class CSVWriter {

    private static HashMap<String, List<Enrollee>> hashMap;

    public static void main(String[] args) throws IOException {

        hashMap  = new HashMap<String,List<Enrollee>>();

        //Reader for CSV Files, current path to Temp File
        Reader reader = new FileReader("C:\\tempFile\\newEnrolleesFile.csv");
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withHeader("userId","name","version","insuranceCompany")
                .withIgnoreHeaderCase()
                .withTrim());

        for (CSVRecord csvRecord : csvParser) {
            // Using Parser to access values by the names assigned to each column
            boolean isNew = true;
            String userId = csvRecord.get("userId");
            String name = csvRecord.get("name");
            Integer version = Integer.parseInt(csvRecord.get("version"));
            String insuranceCompany = csvRecord.get("insuranceCompany");

            //Set Values into Enrollee Object
            Enrollee newEnrollee = new Enrollee();
            newEnrollee.setUserId(userId);
            newEnrollee.setName(name);
            newEnrollee.setVersion(version);
            newEnrollee.setInsuranceCompany(insuranceCompany);

            //Check map if insurance company already exists in map
            if(hashMap.containsKey(insuranceCompany))
            {
                //Loop through Enrollee List to check for existing User Id
                for(int i = 0; i < hashMap.get(insuranceCompany).size(); i++)
                {
                    if(hashMap.get(insuranceCompany).get(i).getUserId().equals(newEnrollee.getUserId()))
                    {
                        //Check if new Enrollee entry has latest Version
                        if(hashMap.get(insuranceCompany).get(i).getVersion() < newEnrollee.getVersion())
                        {
                            hashMap.get(insuranceCompany).set(i, newEnrollee);
                            isNew = false;
                            break;
                        }
                    }
                }
                //Add New Enrollee object
                if(isNew) {
                    hashMap.get(insuranceCompany).add(newEnrollee);
                }
            }
            else
            {
                //Create new Enrollee list and map to insurance company
                List<Enrollee> newList = new ArrayList<Enrollee>();
                newList.add(newEnrollee);
                hashMap.put(insuranceCompany, newList);
            }
        }

        //File Number for File Name
        int fileNum = 1;
        //Iterate through Map for List of Enrollees
        for (Map.Entry<String,List<Enrollee>> entry : hashMap.entrySet())
        {
            //Sort Enrollee List by Names
            Collections.sort(hashMap.get(entry.getValue().get(0).getInsuranceCompany()), compareByName);

            String fileName="C:\\tempFile\\insuranceFile"+fileNum+".csv";
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter writer = new BufferedWriter(fw);
            //Printer for new CSV files
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("userId","name","version","insuranceCompany"));
            for(int i = 0; i < hashMap.get(entry.getValue().get(0).getInsuranceCompany()).size(); i++)
            {
                //Create new record for csv file
                csvPrinter.printRecord(entry.getValue().get(i).getUserId(), entry.getValue().get(i).getName(),
                        entry.getValue().get(i).getVersion(), entry.getValue().get(i).getInsuranceCompany());
            }
            //Increment File Number for name
            fileNum++;
            csvPrinter.flush();
        }
    }

    public HashMap<String, List<Enrollee>> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, List<Enrollee>> hashMap) {
        this.hashMap = hashMap;
    }

    //Comparator to sort Enrollees by Name
    static Comparator<Enrollee> compareByName = new Comparator<Enrollee>() {
        @Override
        public int compare(Enrollee o1, Enrollee o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
}
