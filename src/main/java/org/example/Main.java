package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String CSVroot = "data.csv";
        String XMLroot = "data.xml";
        String newXMLfile = "data2.json";
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> list = parseCSV(columnMapping, CSVroot);
        String jsonFromCSV = listToJson(list);
        List<Employee> list1 = parseXML(XMLroot);
        String jsonFromXML = listToJson(list1);
        System.out.println(jsonFromCSV);
        writeString(jsonFromXML, newXMLfile);
    }

    public static List<Employee> parseCSV(String[] list, String root) {
        List<Employee> staff = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(root))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(list);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    static List<Employee> parseXML(String fileName) throws IOException, SAXException, ParserConfigurationException {
        List<Employee> result = new ArrayList();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            long id = 0L;
            String firstName = null, lastName = null, country = null;
            int age = 0;
            if (Node.ELEMENT_NODE == nodeList.item(i).getNodeType()) {
                NodeList employeeFields = nodeList.item(i).getChildNodes();
                for (int j = 0; j < employeeFields.getLength(); j++) {
                    if (Node.ELEMENT_NODE == employeeFields.item(j).getNodeType()) {
                        String attrName = employeeFields.item(j).getNodeName();
                        String attrValue = employeeFields.item(j).getTextContent();
                        if (attrName.equals("id")) {
                            id = Long.parseLong(attrValue);
                        } else if (attrName.equals("firstName")) {
                            firstName = attrValue;
                        } else if (attrName.equals("lastName")) {
                            lastName = attrValue;
                        } else if (attrName.equals("country")) {
                            country = attrValue;
                        } else if (attrName.equals("age")) {
                            age = Integer.parseInt(attrValue);
                        }
                    }
                }
                result.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return result;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String result = gson.toJson(list);
        return result;
    }

    public static void writeString(String json, String resultFileName) {
        try (FileWriter writer = new FileWriter(resultFileName, false)) {
            writer.write(json);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}